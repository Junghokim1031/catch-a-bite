package com.deliveryapp.catchabite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "deliverer_settlement_item",
    uniqueConstraints = {   // 중복 정산 방지를 위해 delivery_id를 유일키(uk)로 지정
        @UniqueConstraint(name = "uk_settlement_item_delivery", columnNames = {"delivery_id"})
    }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererSettlementItem {  // 정산 리인

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_item_id")
    private Long settlementItemId;

    // FK (DelivererSettlement의 PK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private DelivererSettlement settlement;

    // FK (OrderDelivery의 PK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private OrderDelivery orderDelivery;

    // ================================================= 정산 스냅샷 ======================================
    // 거리(단위:meter)
    @Column(name = "distance_m", nullable = false)
    private Long distanceM;

    // 기본 적용 금액
    @Column(name = "applied_base_fee", nullable = false)
    private Long appliedBaseFee;

    // km당 추가되는 금액
    @Column(name = "applied_per_km_fee", nullable = false)
    private Long appliedPerKmFee;

    // 총 소득
    @Column(name = "earning_amount", nullable = false)
    private Long earningAmount;

    // 배달 완료한 시간
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    // ===================================================================================================

    @PrePersist
    void prePersist() {
        // 거리는 기본값 0(Long 타입)으로 할당.
        if (distanceM == null) distanceM = 0L;
        // 총 소득은 기본값 0(Long 타입)으로 할당.
        if (earningAmount == null) earningAmount = 0L;
    }
}
