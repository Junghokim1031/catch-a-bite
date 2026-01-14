package com.deliveryapp.catchabite.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer_payment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliverer_payment_id")
    private Long delivererPaymentId;

    // deliverer_id는 deliverer.getDelivererId()로 꺼낸다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deliverer_id", nullable = false)
    private Deliverer deliverer;

    // 최소 요금
    @Column(name = "deliverer_payment_minimum_fee", nullable = false)
    private Long delivererPaymentMinimumFee;

    // 거리 당 추가 요금
    @Column(name = "deliverer_payment_distance_fee")
    private Long delivererPaymentDistanceFee;
}