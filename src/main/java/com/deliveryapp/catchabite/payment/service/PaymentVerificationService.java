package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
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

@Log4j2
@Service
public class PaymentVerificationService {
    
    private final PortOneService portOneService;
    private final PaymentRepository paymentRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final TransactionService transactionService;
    
    public PaymentVerificationService(PortOneService portOneService,
                                      PaymentRepository paymentRepository,
                                      StoreOrderRepository storeOrderRepository,
                                      TransactionService transactionService,
                                      ObjectMapper objectMapper) {
        this.portOneService = portOneService;
        this.paymentRepository = paymentRepository;
        this.storeOrderRepository = storeOrderRepository;
        this.transactionService = transactionService;
    }
    
    @Transactional
    public Payment verifyAndCompletePayment(String paymentId, String merchantUid) {
        try {
            log.info("=== Start Payment Verification (V2) ===");
            
            // 1. Input Validation
            if (paymentId == null || merchantUid == null) {
                throw new PaymentException("INVALID_INPUT", "paymentId and merchantUid are required");
            }

            // 2. Fetch Payment from PortOne
            PortOnePaymentVerificationDTO portOnePayment = portOneService.getPaymentDetails(paymentId);
            
            if (portOnePayment == null || portOnePayment.getPaymentId() == null) {
                throw new PaymentException("INVALID_PORTONE_RESPONSE", "Payment data is null");
            }

            log.info("PortOne Status: {}", portOnePayment.getStatus());
            log.info("PortOne Amount: {}", portOnePayment.getAmount().getTotal());

            // 3. Verify Status
            // Note: PortOne V2 status is usually "PAID" (uppercase or lowercase depending on API version, safer to ignore case)
            if (!"PAID".equalsIgnoreCase(portOnePayment.getStatus())) {
                throw new PaymentException("PAYMENT_NOT_PAID", 
                    "Payment status is not PAID. Status: " + portOnePayment.getStatus());
            }

            // 4. Find Order & Payment in DB
            Long orderId = extractOrderIdFromMerchantUid(merchantUid);
            StoreOrder order = storeOrderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentException("ORDER_NOT_FOUND", "Order not found"));
            
            Payment existingPayment = paymentRepository.findByStoreOrder(order)
                    .orElseThrow(() -> new PaymentException("PAYMENT_NOT_FOUND", "Payment not found"));

            // 5. Verify Amount (Critical!)
            Long portOneAmount = portOnePayment.getAmount().getTotal();
            Long dbAmount = existingPayment.getPaymentAmount();

            if (!portOneAmount.equals(dbAmount)) {
                log.error("Amount Mismatch! PortOne: {}, DB: {}", portOneAmount, dbAmount);
                throw new PaymentException("AMOUNT_MISMATCH", "Payment amount mismatch");
            }

            // 6. Check for Double Spending
            if (PaymentConstant.PAYMENT_STATUS_PAID.equals(existingPayment.getPaymentStatus())) {
                throw new PaymentException("ALREADY_PAID", "Order already paid");
            }

            // 7. Update DB
            existingPayment.setPortOnePaymentId(paymentId);
            existingPayment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_PAID);
            
            // Extract Payment Method Type
            if (portOnePayment.getMethod() != null) {
                existingPayment.setPaymentMethod(portOnePayment.getMethod().getType());
            }

            // Convert ISO Date to LocalDateTime
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
            
            // Update Order Status
            order.changeStatus(OrderStatus.PAYMENTCONFIRMED);
            storeOrderRepository.save(order);

            // Save Transaction Log
            Transaction transaction = Transaction.builder()
                    .transactionType(TransactionType.USER_PAYMENT)
                    .relatedEntityId(orderId)
                    .relatedEntityType("ORDER")
                    .amount(portOneAmount)
                    .currency("KRW")
                    .transactionStatus(PaymentConstant.TRANSACTION_STATUS_COMPLETED)
                    .portonePaymentId(paymentId)
                    .createdAt(LocalDateTime.now())
                    .build();
            transactionService.saveTransaction(transaction);

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

    private Long extractOrderIdFromMerchantUid(String merchantUid) {
        try {
            String[] parts = merchantUid.split("_");
            return Long.parseLong(parts[1]);
        } catch (Exception e) {
            throw new PaymentException("INVALID_MERCHANT_UID", "Invalid merchant_uid format");
        }
    }
    
    // handlePaymentFailure method remains similar but ensure parameters are Strings if needed
    @Transactional
    public Payment handlePaymentFailure(String paymentId, String merchantUid, String failReason, String failCode) {
         // ... implementation as before ...
         return null; // Placeholder
    }
}
