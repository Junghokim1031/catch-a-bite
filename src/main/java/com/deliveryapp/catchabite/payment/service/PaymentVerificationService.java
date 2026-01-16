package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentVerificationDTO;
import com.deliveryapp.catchabite.payment.repository.PaymentRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.transaction.entity.Transaction;
import com.deliveryapp.catchabite.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * PaymentVerificationService: PortOne V2 결제 검증 서비스
 * 
 * Description: PortOne V2 API에서 조회한 결제 정보와 DB 정보를 비교하여 검증합니다.
 * payment_id를 사용하여 결제 상세 정보를 조회하고, 금액, 상태, 주문 정보 등을 확인하며
 * 결제를 최종 확정합니다.
 * 
 * Required Variables/Parameters:
 * - portOneService (PortOneService): PortOne V2 API 호출용 (payment_id 기반)
 * - paymentRepository (PaymentRepository): Payment 테이블 조회/수정
 * - storeOrderRepository (StoreOrderRepository): StoreOrder 테이블 조회/수정
 * - transactionService (TransactionService): 거래 기록 저장
 * 
 * Output/Data Flow:
 * - Receives paymentId (V2) from PortOne response
 * - Queries PortOne V2 API using payment_id
 * - Verifies against DB Payment and StoreOrder
 * - Updates Payment.paymentId (stores V2 payment_id), paymentStatus to PAID
 * - Updates StoreOrder.orderStatus to CONFIRMED
 * - Sends Transaction record to TransactionService
 * 
 * Dependencies: PortOneService, PaymentRepository, StoreOrderRepository,
 *               TransactionService, PaymentConstant
 */

@Log4j2
@Service
public class PaymentVerificationService {
    
    private final ObjectMapper objectMapper;
    private final PortOneService portOneService;
    private final PaymentRepository paymentRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final TransactionService transactionService;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param portOneService PortOne V2 API 호출 서비스
     * @param paymentRepository Payment 조회/수정용 Repository
     * @param storeOrderRepository StoreOrder 조회/수정용 Repository
     * @param transactionService 거래 기록 저장용 서비스
     * @param objectMapper JSON 직렬화/역직렬화용
     */
    public PaymentVerificationService(
            PortOneService portOneService,
            PaymentRepository paymentRepository,
            StoreOrderRepository storeOrderRepository,
            TransactionService transactionService,
            ObjectMapper objectMapper) {
        this.portOneService = portOneService;
        this.paymentRepository = paymentRepository;
        this.storeOrderRepository = storeOrderRepository;
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * 결제 검증 및 최종 확정 (PortOne V2)
     * PortOne V2 API에서 payment_id를 사용하여 결제 정보를 조회하고 검증합니다.
     * 
     * V2 변경 사항:
     * - payment_id를 사용하여 결제 정보 조회
     * - merchant_uid는 주문 조회에만 사용 (PortOne 식별용 아님)
     * - Payment 엔티티에 V2 payment_id 저장
     * 
     * @param paymentId PortOne V2 결제 ID (요청 시 전달한 ID)
     * @param merchantUid 상점 주문 번호 (우리 DB에서 주문 찾기용)
     * @return 검증 완료된 Payment 객체
     * @throws PaymentException 검증 실패 시
     */
    @Transactional
    public Payment verifyAndCompletePayment(Long paymentId, String merchantUid) {
        try {
            log.info("========================= PaymentVerificationService.verifyAndCompletePayment() START =========================");
            log.info("Starting payment verification (PortOne V2)");
            log.info("  - payment_id: {} (PortOne V2 결제 ID)", paymentId);
            log.info("  - merchant_uid: {} (상점 주문 번호)", merchantUid);
            
            // ========== Step 1: 입력 검증 ==========
            if (paymentId == null) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[MISSING_PAYMENT_ID] payment_id가 제공되지 않았습니다.");
                log.error("PortOne V2 API 조회에 필수입니다.");
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "MISSING_PAYMENT_ID",
                        "payment_id is required for PortOne V2 verification"
                );
            }
            
            if (merchantUid == null || merchantUid.isEmpty()) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[MISSING_MERCHANT_UID] merchant_uid가 제공되지 않았습니다.");
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "MISSING_MERCHANT_UID",
                        "merchant_uid is required for order verification"
                );
            }
            
            log.info("입력 검증 완료 - payment_id와 merchant_uid가 모두 제공됨");
            
            // ========== Step 2: PortOne V2 API에서 결제 정보 조회 ==========
            log.info("Step 2: PortOne V2 API에서 결제 정보 조회 중...");
            
            PortOnePaymentVerificationDTO portOnePayment = 
                    portOneService.getPaymentDetails(paymentId);  // ← V2 메서드 호출
            
            log.info("PortOne V2 응답 수신:");
            log.info("  - code: {}", portOnePayment.getCode());
            log.info("  - message: {}", portOnePayment.getMessage());
            
            PortOnePaymentVerificationDTO.PaymentData paymentData = 
                    portOnePayment.getResponse();
            
            if (paymentData == null) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[NULL_PAYMENT_DATA] PortOne V2 응답의 payment data가 null입니다.");
                log.error("Full PortOne Response: {}", 
                        objectMapper.writeValueAsString(portOnePayment));
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "INVALID_PORTONE_RESPONSE",
                        "PortOne V2 payment data is null"
                );
            }
            
            log.info("PortOne 결제 정보:");
            log.info("  - payment_id: {}", paymentData.getPaymentId());
            log.info("  - status: {}", paymentData.getStatus());
            log.info("  - amount: {}", paymentData.getAmount());
            log.info("  - pay_method: {}", paymentData.getPayMethod());
            log.info("  - merchant_uid: {}", paymentData.getMerchantUid());
            log.info("  - paid_at: {}", paymentData.getPaidAt());
            
            // ========== Step 3: PortOne 결제 상태 확인 (매우 중요!) ==========
            log.info("Step 3: PortOne 결제 상태 검증 중...");
            
            String portOneStatus = paymentData.getStatus();
            if (!"paid".equals(portOneStatus)) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[PAYMENT_NOT_PAID] PortOne V2에서 결제 상태가 'paid'가 아닙니다.");
                log.error("  - payment_id: {}", paymentId);
                log.error("  - current_status: {}", portOneStatus);
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "PAYMENT_NOT_PAID",
                        "PortOne payment status is not 'paid'. Status: " + portOneStatus
                );
            }
            
            log.info("PortOne 결제 상태 검증 완료 - status = PAID ✓");
            
            // ========== Step 4: DB에서 주문 및 기존 결제 정보 조회 ==========
            log.info("Step 4: DB에서 주문 정보 조회 중...");
            
            // merchantUid 형식: "ORDER_" + orderId + "_" + timestamp
            Long orderId = extractOrderIdFromMerchantUid(merchantUid);

            if(orderId == null){
                throw new IllegalArgumentException("PaymentVerification - verifyAndCompletePayment OrderId " + orderId +"가 null입니다");
            }

            log.info("  - 추출된 Order ID: {}", orderId);
            
            StoreOrder order = storeOrderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                        log.error("[ORDER_NOT_FOUND] Order를 DB에서 찾을 수 없습니다.");
                        log.error("  - order_id: {}", orderId);
                        log.error("  - merchant_uid: {}", merchantUid);
                        log.error("=========================================================================================");
                        
                        return new PaymentException(
                                "ORDER_NOT_FOUND",
                                PaymentConstant.ERROR_ORDER_NOT_FOUND
                        );
                    });
            
            log.info("Order 조회 완료:");
            log.info("  - order_id: {}", order.getOrderId());
            log.info("  - order_status: {}", order.getOrderStatus());
            log.info("  - total_price: {}", order.getOrderTotalPrice());
            
            Payment existingPayment = paymentRepository.findByStoreOrder(order)
                    .orElseThrow(() -> {
                        log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                        log.error("[PAYMENT_NOT_FOUND] Payment를 DB에서 찾을 수 없습니다.");
                        log.error("  - order_id: {}", orderId);
                        log.error("=========================================================================================");
                        
                        return new PaymentException(
                                "PAYMENT_NOT_FOUND",
                                PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                        );
                    });
            
            log.info("Payment 조회 완료:");
            log.info("  - payment_id (DB): {}", existingPayment.getPaymentId());
            log.info("  - payment_status: {}", existingPayment.getPaymentStatus());
            log.info("  - payment_amount: {}", existingPayment.getPaymentAmount());
            
            // ========== Step 5: merchant_uid 검증 ==========
            log.info("Step 5: merchant_uid 검증 중...");
            
            String portOneMerchantUid = paymentData.getMerchantUid();
            if (!merchantUid.equals(portOneMerchantUid)) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[MERCHANT_UID_MISMATCH] merchant_uid가 일치하지 않습니다.");
                log.error("  - 요청한 merchant_uid: {}", merchantUid);
                log.error("  - PortOne 응답 merchant_uid: {}", portOneMerchantUid);
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "MERCHANT_UID_MISMATCH",
                        "Merchant UID mismatch. Expected: " + merchantUid + 
                        ", Got: " + portOneMerchantUid
                );
            }
            
            log.info("merchant_uid 검증 완료 - 일치함 ✓");
            
            // ========== Step 6: 금액 검증 (매우 중요!) ==========
            log.info("Step 6: 금액 검증 중...");
            
            Long portOneAmount = paymentData.getAmount();
            Long dbAmount = existingPayment.getPaymentAmount();
            
            log.info("  - PortOne V2 금액: {}", portOneAmount);
            log.info("  - DB 금액: {}", dbAmount);
            
            if (!portOneAmount.equals(dbAmount)) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[AMOUNT_MISMATCH] 금액이 일치하지 않습니다. 결제 사기 가능성!");
                log.error("  - PortOne 금액: {}", portOneAmount);
                log.error("  - DB 금액: {}", dbAmount);
                log.error("  - 차이: {}", Math.abs(portOneAmount - dbAmount));
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "AMOUNT_MISMATCH",
                        PaymentConstant.ERROR_AMOUNT_MISMATCH + 
                        " PortOne: " + portOneAmount + ", DB: " + dbAmount
                );
            }
            
            log.info("금액 검증 완료 - 일치함 ✓");
            
            // ========== Step 7: 이미 결제 완료된 것은 아닌지 확인 ==========
            log.info("Step 7: 중복 결제 확인 중...");
            
            if (PaymentConstant.PAYMENT_STATUS_PAID.equals(
                    existingPayment.getPaymentStatus())) {
                log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
                log.error("[PAYMENT_ALREADY_PAID] 이미 결제가 완료된 주문입니다.");
                log.error("  - order_id: {}", orderId);
                log.error("  - payment_status: {}", existingPayment.getPaymentStatus());
                log.error("=========================================================================================");
                
                throw new PaymentException(
                        "PAYMENT_ALREADY_PAID",
                        PaymentConstant.ERROR_PAYMENT_ALREADY_COMPLETED
                );
            }
            
            log.info("중복 결제 확인 완료 - 정상 ✓");
            
            // ========== Step 8: Payment 엔티티 업데이트 (V2 key changes) ==========
            log.info("Step 8: Payment 엔티티 업데이트 중...");
            
            // V2: payment_id 저장 (PortOne V2 응답에서 얻은 값)
            existingPayment.setPaymentId(paymentData.getPaymentId());
            
            // V2: status 업데이트
            existingPayment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_PAID);
            existingPayment.setPaymentMethod(paymentData.getPayMethod());
            
            // V2: paid_at 저장 (Unix timestamp를 LocalDateTime으로 변환)
            if (paymentData.getPaidAt() != null) {
                LocalDateTime paidAt = Instant.ofEpochSecond(paymentData.getPaidAt())
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();
                existingPayment.setPaymentPaidAt(paidAt);
                log.info("  - 결제 완료 시간: {}", paidAt);
            }
            
            Payment savedPayment = paymentRepository.save(existingPayment);
            log.info("Payment 저장 완료:");
            log.info("  - payment_id: {}", savedPayment.getPaymentId());
            log.info("  - payment_status: PAID");
            log.info("  - payment_amount: {}", savedPayment.getPaymentAmount());
            
            // ========== Step 9: StoreOrder 상태 업데이트 ==========
            log.info("Step 9: StoreOrder 상태 업데이트 중...");
            
            order.changeStatus(OrderStatus.PAYMENTCONFIRMED);
            storeOrderRepository.save(order);
            log.info("Order 저장 완료:");
            log.info("  - order_id: {}", order.getOrderId());
            log.info("  - order_status: CONFIRMED");
            
            // ========== Step 10: Transaction 기록 저장 ==========
            log.info("Step 10: Transaction 기록 저장 중...");
            
            Transaction transaction = Transaction.builder()
                    .transactionType(com.deliveryapp.catchabite.transaction.entity.TransactionType.USER_PAYMENT)
                    .relatedEntityId(orderId)
                    .relatedEntityType("ORDER")
                    .amount(portOneAmount)
                    .currency("KRW")
                    .transactionStatus(PaymentConstant.TRANSACTION_STATUS_COMPLETED)
                    .portonePaymentId(String.valueOf(paymentId))  
                    .createdAt(LocalDateTime.now())
                    .completedAt(LocalDateTime.now())
                    .build();
            
            transactionService.saveTransaction(transaction);
            log.info("Transaction 저장 완료");
            
            log.info("========================= PaymentVerificationService.verifyAndCompletePayment() SUCCESS =========================");
            log.info("결제 검증 및 완료 성공!");
            log.info("  - payment_id: {}", savedPayment.getPaymentId());
            log.info("  - order_id: {}", orderId);
            log.info("  - amount: {}", portOneAmount);
            log.info("=========================================================================================");
            
            return savedPayment;
            
        } catch (PaymentException pe) {
            log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
            log.error("PaymentException 발생:");
            log.error("  - error_code: {}", pe.getErrorCode());
            log.error("  - error_message: {}", pe.getErrorMessage());
            log.error("=========================================================================================");
            throw pe;
            
        } catch (Exception e) {
            log.error("========================= PaymentVerificationService.verifyAndCompletePayment() ERROR =========================");
            log.error("예상치 못한 Exception 발생:");
            log.error("  - exception_type: {}", e.getClass().getName());
            log.error("  - message: {}", e.getMessage());
            log.error("=========================================================================================");
            log.error("Stack Trace: ", e);
            
            throw new PaymentException(
                    "VERIFICATION_ERROR",
                    "Unexpected error during payment verification",
                    e
            );
        }
    }
    
    /**
     * merchantUid에서 orderId 추출
     * merchantUid 형식: "ORDER_" + orderId + "_" + timestamp
     * 예: "ORDER_123_1705085400000"
     * 
     * @param merchantUid 상점 주문 번호
     * @return 추출된 orderId
     * @throws PaymentException 형식이 잘못된 경우
     */
    private Long extractOrderIdFromMerchantUid(String merchantUid) {
        try {
            log.debug("merchantUid 파싱 중: {}", merchantUid);
            
            String[] parts = merchantUid.split("_");
            if (parts.length < 2) {
                log.error("merchantUid 형식이 잘못되었습니다: {}", merchantUid);
                throw new PaymentException(
                        "INVALID_MERCHANT_UID_FORMAT",
                        PaymentConstant.ERROR_INVALID_MERCHANT_UID
                );
            }
            
            Long orderId = Long.parseLong(parts[1]);
            log.debug("추출된 Order ID: {}", orderId);
            return orderId;
            
        } catch (NumberFormatException e) {
            log.error("Order ID 파싱 실패: {}", merchantUid);
            throw new PaymentException(
                    "INVALID_MERCHANT_UID_FORMAT",
                    PaymentConstant.ERROR_INVALID_MERCHANT_UID,
                    e
            );
        }
    }
    
    /**
     * 결제 실패 처리 (V2)
     * 결제 실패 시 Payment 상태를 FAILED로 업데이트합니다.
     * 
     * @param paymentId PortOne V2 결제 ID
     * @param merchantUid 상점 주문 번호
     * @param failReason 실패 사유
     * @param failCode PortOne 실패 코드
     * @return 업데이트된 Payment 객체
     */
    @Transactional
    public Payment handlePaymentFailure(Long paymentId,
                                          String merchantUid,
                                          String failReason,
                                          String failCode) {
        try {
            log.error("========================= PaymentVerificationService.handlePaymentFailureV2() START =========================");
            log.error("결제 실패 처리 시작:");
            log.error("  - payment_id: {}", paymentId);
            log.error("  - merchant_uid: {}", merchantUid);
            log.error("  - fail_reason: {}", failReason);
            log.error("  - fail_code: {}", failCode);
            
            Long orderId = extractOrderIdFromMerchantUid(merchantUid);
            
            StoreOrder order = storeOrderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("[ORDER_NOT_FOUND] Order를 DB에서 찾을 수 없습니다.");
                        return new PaymentException(
                                "ORDER_NOT_FOUND",
                                PaymentConstant.ERROR_ORDER_NOT_FOUND
                        );
                    });
            
            Payment payment = paymentRepository.findByStoreOrder(order)
                    .orElseThrow(() -> {
                        log.error("[PAYMENT_NOT_FOUND] Payment를 DB에서 찾을 수 없습니다.");
                        return new PaymentException(
                                "PAYMENT_NOT_FOUND",
                                PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                        );
                    });
            
            // 결제 상태 FAILED로 업데이트
            payment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_FAILED);
            payment.setPaymentId(paymentId); 
            
            Payment savedPayment = paymentRepository.save(payment);
            log.error("Payment 상태 업데이트 완료: FAILED");
            
            // Transaction 기록 저장 (실패 상태로)
            Transaction failedTransaction = Transaction.builder()
                    .transactionType(com.deliveryapp.catchabite.transaction.entity.TransactionType.USER_PAYMENT)
                    .relatedEntityId(orderId)
                    .relatedEntityType("ORDER")
                    .amount(payment.getPaymentAmount())
                    .currency("KRW")
                    .transactionStatus(PaymentConstant.TRANSACTION_STATUS_FAILED)
                    .failureReason(failReason + " (Code: " + failCode + ")")
                    .portonePaymentId(String.valueOf(paymentId)) 
                    .createdAt(LocalDateTime.now())
                    .build();
            
            transactionService.saveTransaction(failedTransaction);
            log.error("Transaction 실패 기록 저장 완료");
            
            log.error("========================= PaymentVerificationService.handlePaymentFailure() END =========================");
            return savedPayment;
            
        } catch (Exception e) {
            log.error("========================= PaymentVerificationService.handlePaymentFailure() ERROR =========================");
            log.error("결제 실패 처리 중 오류 발생");
            log.error("Exception: {}", e.getMessage());
            log.error("=========================================================================================");
            log.error("Stack Trace: ", e);
            
            throw new PaymentException(
                    "FAILURE_HANDLING_ERROR",
                    "Error handling payment failure",
                    e
            );
        }
    }
}
