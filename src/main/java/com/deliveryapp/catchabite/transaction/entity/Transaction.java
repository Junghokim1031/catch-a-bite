package com.deliveryapp.catchabite.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Transaction: PortOne 거래(결제/정산) 기록 엔티티
 * 
 * Description: PortOne 결제(USER_PAYMENT) 및 정산(STORE_PAYOUT, DELIVERY_PAYOUT) 기록을
 * TRANSACTION 테이블에 저장합니다. 주문/가게/라이더 등 “어떤 대상”의 거래인지
 * relatedEntityId + relatedEntityType로 연결합니다.
 * 
 * Required Variables/Parameters:
 * - transactionType (TransactionType): 거래 종류 (USERPAYMENT/STOREPAYOUT/DELIVERYPAYOUT)
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
@Table(name = "TRANSACTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false, length = 50)
    private TransactionType transactionType;

    @Column(name = "TRANSACTION_RELATED_ENTITY_ID", nullable = false)
    private Long relatedEntityId;

    @Column(name = "TRANSACTION_RELATED_ENTITY_TYPE", nullable = false, length = 50)
    private String relatedEntityType;

    @Column(name = "TRANSACTION_AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "TRANSACTION_CURRENCY", nullable = false, length = 10)
    private String currency;

    @Column(name = "TRANSACTION_STATUS", nullable = false, length = 50)
    private String transactionStatus;

    @Column(name = "TRANSACTION_PORTONE_PAYMENT_ID", length = 255)
    private String portonePaymentId;

    @Column(name = "TRANSACTION_PORTONE_TRANSFER_ID", length = 255)
    private String portoneTransferId;

    @Column(name = "TRANSACTION_FAILURE_REASON", length = 500)
    private String failureReason;

    @Column(name = "TRANSACTION_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "TRANSACTION_COMPLETED_AT")
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
