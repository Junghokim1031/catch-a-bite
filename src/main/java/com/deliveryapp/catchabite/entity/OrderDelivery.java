package com.deliveryapp.catchabite.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_delivery")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    // store_order테이블의 PK를 FK로 가져옴
    // order_id는 storeOrder.getStoreOrderID()로 꺼냄
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private StoreOrder storeOrder;

    // 배정 전에는 NULL 가능
    // deliverer_id는 deliverer.getDelivererId()로 꺼낸다.
    // 한 배달기사는 여러 배달을 수행할 수 있음 (M:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "deliverer_id", nullable = true)
    private Deliverer deliverer;

    // 배달 대행 수락한 시간
    @Column(name = "order_accept_time")
    private LocalDateTime orderAcceptTime;

    // 가게에서 픽업 시간
    @Column(name = "order_delivery_pickup_time")
    private LocalDateTime orderDeliveryPickupTime;

    // 배달 시작/완료 시간
    @Column(name = "order_delivery_start_time")
    private LocalDateTime orderDeliveryStartTime;

    @Column(name = "order_delivery_complete_time")
    private LocalDateTime orderDeliveryCompleteTime;

    // 거리/시간(분)
    @Column(name = "order_delivery_distance", precision = 10, scale = 2)
    private BigDecimal orderDeliveryDistance;

    @Column(name = "order_delivery_est_time")
    private Integer orderDeliveryEstTime;

    @Column(name = "order_delivery_act_time")
    private Integer orderDeliveryActTime;

    // 주문 배송 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "order_delivery_status", length = 20)
    private DeliveryStatus orderDeliveryStatus;

    // 배차(배달) 요청이 음식점에서 배달원에게 날라간 시간
    @Column(name = "order_delivery_created_date")
    private LocalDateTime orderDeliveryCreatedDate;

    @PrePersist
    void prePersist() {
        //  주문 배송 상태는 입력된 값이 없으면, 기본값을 대기(PENDING)으로 할당함.
        if (orderDeliveryStatus == null) orderDeliveryStatus = DeliveryStatus.PENDING;
        // 배달기사에게 배달 요청을 보낸 시간이 없으면, 기본값을 현재시간으로 설정함.
        if (orderDeliveryCreatedDate == null) orderDeliveryCreatedDate = LocalDateTime.now();
    }

}