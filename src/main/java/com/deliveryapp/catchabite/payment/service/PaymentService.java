package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.payment.converter.PaymentDTOConverter;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentRequestDTO;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentResponseDTO;
import com.deliveryapp.catchabite.payment.repository.PaymentRepository;

import lombok.extern.log4j.Log4j2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * PaymentService: 결제 준비 및 조율 서비스
 * 
 * Description: 프론트엔드의 결제 요청을 처리하여 PortOne 결제 창을 띄우기 위한
 * 준비 작업을 수행합니다. Payment 엔티티를 생성하고, 주문 정보를 검증합니다.
 * 
 * Required Variables/Parameters:
 * - storeOrderRepository (StoreOrderRepository): 주문 조회
 * - paymentRepository (PaymentRepository): 결제 저장
 * - paymentDTOConverter (PaymentDTOConverter): DTO 변환
 * - portOneImpKey (String): PortOne Imp Key (application.properties)
 * - portOneApiUrl (String): PortOne API URL
 * 
 * Output/Data Flow:
 * - Receives PortOnePaymentRequestDTO from PaymentController
 * - Creates Payment entity with PENDING status
 * - Sends PortOnePaymentResponseDTO to controller
 * - Controller sends to React Native frontend
 * 
 * Dependencies: PaymentRepository, StoreOrderRepository,
 *               PaymentDTOConverter, PaymentConstant
 */

@Log4j2
@Service
public class PaymentService {
    
    private final StoreOrderRepository storeOrderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentDTOConverter paymentDTOConverter;
    private final AppUserRepository appUserRepository;

    @Value("${portone.store-id}")
    private String portoneImpKey;
    
    @Value("${portone.baseUrl}")
    private String portoneApiUrl;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param storeOrderRepository 주문 조회용 Repository
     * @param paymentRepository 결제 저장용 Repository
     * @param paymentDTOConverter DTO 변환기
     */
    @Autowired
    public PaymentService(
            StoreOrderRepository storeOrderRepository,
            PaymentRepository paymentRepository,
            PaymentDTOConverter paymentDTOConverter, 
            AppUserRepository appUserRepository) {
        this.storeOrderRepository = storeOrderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentDTOConverter = paymentDTOConverter;
        this.appUserRepository = appUserRepository;
    }
    
    /**
     * 결제 준비 - PortOne 결제 창 띄우기 위한 준비
     * 
     * Step 1: 주문 존재 확인
     * Step 2: 주문의 사용자 정보 확인 (StoreOrder에서)
     * Step 3: Payment 엔티티 생성 (상태: PENDING)
     * Step 4: PortOne 응답 DTO 생성
     * 
     * @param request 프론트에서 받은 결제 요청 정보
     * @return PortOne 결제 창에 필요한 응답
     * @throws PaymentException 주문이나 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public PortOnePaymentResponseDTO preparePayment(PortOnePaymentRequestDTO request) {
        
        try {
            log.info("Preparing payment. order_id: {}, amount: {}", 
                    request.getOrderId(), request.getPaymentAmount());
            
            // Step 1: 주문 존재 확인
            StoreOrder order = storeOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new PaymentException(
                            "ORDER_NOT_FOUND",
                            PaymentConstant.ERROR_ORDER_NOT_FOUND
                    ));
            
            log.info("Order found: {}", order.getOrderId());
            
            // Step 2: 주문의 사용자 정보 확인
            AppUser appUser = order.getAppUser();
            if (appUser == null) {
                log.error("AppUser is NULL for order_id: {}", order.getOrderId());
                throw new PaymentException(
                        "USER_NOT_FOUND",
                        "User associated with order not found"
                );
            }
            
            log.info("AppUser found: {} (email: {})", appUser.getAppUserNickname(), appUser.getAppUserEmail());
            
            // Step 2-2: Store 확인
            if (order.getStore() == null) {
                log.error("Store is NULL for order_id: {}", order.getOrderId());
                throw new PaymentException(
                        "STORE_NOT_FOUND",
                        "Store associated with order not found"
                );
            }
            
            log.info("Store found: {}", order.getStore().getStoreName());
            
            // Step 3: merchant_uid 생성
            String merchantUid = generateMerchantUid(request.getOrderId());
            log.info("Generated merchant_uid: {}", merchantUid);
            
            // Step 4: Payment 엔티티 생성
            Payment payment = Payment.builder()
                    .storeOrder(order)
                    .paymentMethod(request.getPaymentMethod() != null ? 
                            request.getPaymentMethod() : 
                            PaymentConstant.PAYMENT_METHOD_CARD)
                    .paymentAmount(request.getPaymentAmount())
                    .paymentStatus(PaymentConstant.PAYMENT_STATUS_PENDING)
                    .build();
            
            log.info("Creating Payment entity with status: PENDING");
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved successfully. payment_id: {}", savedPayment.getPaymentId());
            
            // Step 5: PortOne 응답 생성
            PortOnePaymentResponseDTO response = 
                    paymentDTOConverter.toPortOnePaymentResponseDTO(
                            merchantUid,
                            portoneImpKey,
                            request,
                            System.currentTimeMillis()
                    );
            
            log.info("Response DTO created");
            response.setApiEndpoint(portoneApiUrl);
            
            log.info("Payment preparation completed successfully");
            return response;
            
        } catch (PaymentException pe) {
            log.error("Payment preparation failed: {}", pe.getErrorMessage());
            throw pe;
        } catch (Exception e) {
            log.error("❌ Unexpected error during payment preparation", e);  // 🔴 Full stack trace
            throw new PaymentException(
                    "PAYMENT_PREPARATION_ERROR",
                    "Unexpected error during payment preparation",
                    e
            );
        }
    }


    
    /**
     * merchant_uid 생성
     * 형식: "ORDER_" + orderId + "_" + timestamp
     * 예: "ORDER_123_1705085400000"
     * 
     * @param orderId 주문 ID
     * @return 생성된 merchant_uid
     */
    private String generateMerchantUid(Long orderId) {
        return String.format("ORDER_%d_%d", orderId, System.currentTimeMillis());
    }
    
    /**
     * 결제 정보 조회
     * 
     * @param paymentId Payment ID
     * @return 결제 정보
     * @throws PaymentException 결제를 찾을 수 없는 경우
     */
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(
                        "PAYMENT_NOT_FOUND",
                        PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                ));
    }
    
    /**
     * 주문별 결제 정보 조회
     * 
     * @param orderId 주문 ID
     * @return 결제 정보
     * @throws PaymentException 주문이나 결제를 찾을 수 없는 경우
     */
    public Payment getPaymentByOrderId(Long orderId) {
        StoreOrder order = storeOrderRepository.findById(orderId)
                .orElseThrow(() -> new PaymentException(
                        "ORDER_NOT_FOUND",
                        PaymentConstant.ERROR_ORDER_NOT_FOUND
                ));
        
        return paymentRepository.findByStoreOrder(order)
                .orElseThrow(() -> new PaymentException(
                        "PAYMENT_NOT_FOUND",
                        PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                ));
    }
}
