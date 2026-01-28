package com.deliveryapp.catchabite.transaction.entity;

import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Transaction: PortOne 거래(결제/정산) 기록 엔티티
 * 
 * Description: PortOne 결제(USER_PAYMENT) 및 정산(STORE_PAYOUT, DELIVERY_PAYOUT) 기록을
 * TRANSACTION 테이블에 저장합니다. 주문/가게/라이더 등 “어떤 대상”의 거래인지
 * relatedEntityId + relatedEntityType로 연결합니다.
 * 
 * Required Variables/Parameters:
 * - transactionType (TransactionType): 거래 종류 (USER_PAYMENT/STORE_PAYOUT/DELIVERY_PAYOUT)
 * - relatedEntityId (Long): 관련 엔티티 ID (예: 주문ID)
 * - relatedEntityType (String): 관련 엔티티 타입 문자열 (예: "ORDER")
 * - amount (Long): 거래 금액 (KRW)
 * - currency (String): 통화 (기본 "KRW")
 * - transactionStatus (String): 상태 (PENDING/COMPLETED/FAILED/REFUNDED)
 * - portonePaymentId (String): PortOne imp_uid (결제일 때)
 * - portoneTransferId (String): PortOne transfer id (정산일 때)
 * 
 * Output/Data Flow:
 * - PaymentVerificationService에서 결제 검증 성공/실패 시 저장됩니다.
 * 
 * Dependencies: JPA, Lombok, TransactionType
 */
@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "transaction_related_entity_id", nullable = false)
    private Long relatedEntityId;

    @Column(name = "transaction_related_entity_type", nullable = false, length = 50)
    private String relatedEntityType;

    @Column(name = "transaction_amount", nullable = false)
    private Long amount;

    @Column(name = "transaction_currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "transaction_status", nullable = false, length = 50)
    private String transactionStatus;

    @Column(name = "transaction_portone_payment_id", length = 255)
    private String portonePaymentId;

    @Column(name = "transaction_portone_transfer_id", length = 255)
    private String portoneTransferId;

    @Column(name = "transaction_failure_reason", length = 500)
    private String failureReason;

    @Column(name = "transaction_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "transaction_completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (currency == null || currency.isBlank()) {
            currency = "KRW";
        }
    }
}
