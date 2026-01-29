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
import com.deliveryapp.catchabite.service.OwnerSettlementItemService;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@Service
public class PaymentVerificationService {

	private final PortOneService portOneService;
	private final PaymentRepository paymentRepository;
	private final StoreOrderRepository storeOrderRepository;
	private final TransactionService transactionService;
	private final OwnerSettlementItemService ownerSettlementItemService;

	public PaymentVerificationService(
			PortOneService portOneService,
			PaymentRepository paymentRepository,
			StoreOrderRepository storeOrderRepository,
			TransactionService transactionService,
			OwnerSettlementItemService ownerSettlementItemService
	) {
		this.portOneService = portOneService;
		this.paymentRepository = paymentRepository;
		this.storeOrderRepository = storeOrderRepository;
		this.transactionService = transactionService;
		this.ownerSettlementItemService = ownerSettlementItemService;
	}

	@Transactional
	public Payment verifyAndCompletePayment(String paymentId, String merchantUid) {
		try {
			log.info("=== Start Payment Verification (V2) ===");

			/*
            ========================================================================================
            [1. 입력값 검증]
            결제 ID와 가맹점 주문 번호(merchantUid)가 존재하는지 확인합니다.
            필수 값이 누락된 경우 예외를 발생시킵니다.
            ========================================================================================
             */
			if (paymentId == null || merchantUid == null) {
				throw new PaymentException("INVALID_INPUT", "paymentId and merchantUid are required");
			}

			/*
            ========================================================================================
            [2. PortOne 결제 정보 조회]
            PortOne API를 호출하여 결제 상세 정보를 가져옵니다.
            응답 데이터가 없거나 결제 ID가 확인되지 않으면 예외 처리합니다.
            ========================================================================================
             */
			PortOnePaymentVerificationDTO portOnePayment = portOneService.getPaymentDetails(paymentId);

			if (portOnePayment == null || portOnePayment.getPaymentId() == null) {
				throw new PaymentException("INVALID_PORTONE_RESPONSE", "Payment data is null");
			}

			log.info("PortOne Status: {}", portOnePayment.getStatus());
			log.info("PortOne Amount: {}", portOnePayment.getAmount().getTotal());

			/*
            ========================================================================================
            [3. 결제 상태 확인]
            PortOne에서 반환된 결제 상태가 'PAID'(결제 완료)인지 확인합니다.
            결제가 완료되지 않은 상태라면 검증을 중단합니다.
            ========================================================================================
             */
			if (!"PAID".equalsIgnoreCase(portOnePayment.getStatus())) {
				throw new PaymentException(
						"PAYMENT_NOT_PAID",
						"Payment status is not PAID. Status: " + portOnePayment.getStatus()
				);
			}

			/*
            ========================================================================================
            [4. DB 주문 및 결제 데이터 조회]
            merchantUid에서 주문 ID를 추출하여 DB에 저장된 주문(StoreOrder)과
            해당 주문에 연결된 기존 결제(Payment) 엔티티를 조회합니다.
            ========================================================================================
             */
			Long orderId = extractOrderIdFromMerchantUid(merchantUid);
			StoreOrder order = storeOrderRepository.findById(orderId)
					.orElseThrow(() -> new PaymentException("ORDER_NOT_FOUND", "Order not found"));

			Payment existingPayment = paymentRepository.findByStoreOrder(order)
					.orElseThrow(() -> new PaymentException("PAYMENT_NOT_FOUND", "Payment not found"));

			/*
            ========================================================================================
            [5. 결제 금액 위변조 검증]
            PortOne에 실제 결제된 금액과 DB에 저장된 결제 예정 금액이 일치하는지 비교합니다.
            금액이 다를 경우 결제 위변조로 간주하여 예외를 발생시킵니다.
            ========================================================================================
             */
			Long portOneAmount = portOnePayment.getAmount().getTotal();
			Long dbAmount = existingPayment.getPaymentAmount();

			if (!portOneAmount.equals(dbAmount)) {
				log.error("Amount Mismatch! PortOne: {}, DB: {}", portOneAmount, dbAmount);
				throw new PaymentException("AMOUNT_MISMATCH", "Payment amount mismatch");
			}

			/*
            ========================================================================================
            [6. 중복 결제(Double Spending) 방지]
            이미 'PAID' 상태로 처리된 결제 건인지 확인합니다.
            ========================================================================================
             */
			if (PaymentConstant.PAYMENT_STATUS_PAID.equals(existingPayment.getPaymentStatus())) {
				throw new PaymentException("ALREADY_PAID", "Order already paid");
			}

			/*
            ========================================================================================
            [7. DB 업데이트 - 결제 정보]
            검증이 완료된 결제 정보를 업데이트합니다.
            - PortOne 결제 ID 저장
            - 결제 상태를 PAID로 변경
            - 결제 수단(카드 등) 및 결제 완료 시간(ISO -> LocalDateTime 변환) 저장
            ========================================================================================
             */
			existingPayment.setPortOnePaymentId(paymentId);
			existingPayment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_PAID);

			// 결제 수단 타입 추출 및 설정
			if (portOnePayment.getMethod() != null) {
				existingPayment.setPaymentMethod(portOnePayment.getMethod().getType());
			}

			// 결제 시간 변환 및 설정
			if (portOnePayment.getPaidAt() != null) {
				try {
					LocalDateTime paidAt = Instant.parse(portOnePayment.getPaidAt())
							.atZone(ZoneId.of("Asia/Seoul"))
							.toLocalDateTime();
					existingPayment.setPaymentPaidAt(paidAt);
				} catch (Exception e) {
					log.warn("Failed to parse paidAt date: {}", portOnePayment.getPaidAt());
					existingPayment.setPaymentPaidAt(LocalDateTime.now());
				}
			}

			Payment savedPayment = paymentRepository.save(existingPayment);

			/*
            ========================================================================================
            [8. DB 업데이트 - 주문 상태 변경]
            결제가 성공적으로 완료되었으므로 주문 상태를 변경합니다.
            요청에 따라 'PENDING' 상태로 설정합니다.
            ========================================================================================
             */
			order.changeStatus(OrderStatus.PAYMENTCONFIRMED);
			storeOrderRepository.save(order);

			/*
            ========================================================================================
            [9. 트랜잭션 로그 및 정산 데이터 기록]
            - 사용자 결제 기록(Transaction) 생성
            - 사장님 정산(Settlement Item) 데이터 생성 (중복 방지 로직 포함)
            ========================================================================================
             */
			Transaction transaction = Transaction.builder()
					.transactionType(com.deliveryapp.catchabite.domain.enumtype.TransactionType.USER_PAYMENT)
					.relatedEntityId(orderId)
					.relatedEntityType("ORDER")
					.amount(portOneAmount)
					.currency("KRW")
					.transactionStatus(PaymentConstant.TRANSACTION_STATUS_COMPLETED)
					.portonePaymentId(paymentId)
					.createdAt(LocalDateTime.now())
					.build();
			transactionService.saveTransaction(transaction);

			// 사업자 정산(주문별 라인) 기록 (중복 생성 방지)
			ownerSettlementItemService.recordPaidOrder(orderId);

			log.info("=== Payment Verification Success ===");
			return savedPayment;

		} catch (PaymentException pe) {
			log.error("Payment Verification Failed: {}", pe.getMessage());
			throw pe;
		} catch (Exception e) {
			log.error("Unexpected Error", e);
			throw new PaymentException("VERIFICATION_ERROR", "Unexpected error", e);
		}
	}

	/*
    ========================================================================================
    [헬퍼 메서드: 주문 ID 추출]
    merchant_uid 형식(예: ORDER_101_172345)에서 실제 주문 ID(101)를 파싱합니다.
    형식이 올바르지 않으면 예외를 발생시킵니다.
    ========================================================================================
     */
	private Long extractOrderIdFromMerchantUid(String merchantUid) {
		try {
			String[] parts = merchantUid.split("_");
			return Long.parseLong(parts[1]);
		} catch (Exception e) {
			throw new PaymentException("INVALID_MERCHANT_UID", "Invalid merchant_uid format");
		}
	}

	@Transactional
	public Payment handlePaymentFailure(String paymentId, String merchantUid, String failReason, String failCode) {
		// TODO: 필요 시 기존 구현을 이 위치에 
		return null;
	}
}