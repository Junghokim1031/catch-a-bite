package com.deliveryapp.catchabite.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.SettlementStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer_settlement")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererSettlement {  // 정산 헤더
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long settlementId;

    // FK(deliverer 테이블에서 가져옴)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deliverer_id", nullable = false)
    private Deliverer deliverer;

    // 기간 시작
    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;

    // 기간 끝
    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    // 총 정산금액
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    // 정산 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false, length = 30)
    private SettlementStatus settlementStatus;

    // 정산 요청 날짜
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    // 지급 날짜
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // 메모
    @Column(name = "note", length = 255)
    private String note;

    @PrePersist
    void prePersist() {
        // 정산 요청 날짜가 없으면, 현재 시간으로 할당한다.
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        // 정산 상태는 값이 없을 때, 'REQUESTED(요청됨)'로 할당한다.
        if (settlementStatus == null) settlementStatus = SettlementStatus.REQUESTED;
        // 정산 금액은 기본값을 '0'(Long 타입)으로 할당한다.
        if (totalAmount == null) totalAmount = 0L;
    }
}
