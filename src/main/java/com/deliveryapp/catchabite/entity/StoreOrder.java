package com.deliveryapp.catchabite.entity;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * 이 클래스는 일부러 @Setter를 작성하지 않았습니다.
 * @Builder만 사용함으로 null이 발생하는 것을 방지하고자 합니다.
 */
@Entity
@Table(name = "store_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false) // ERD shows nullable
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ EAGER -> LAZY 권장 (N+1 방지)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "order_address_snapshot", nullable = false, length = 255)
    private String orderAddressSnapshot;

    @Column(name = "order_total_price", nullable = false)
    private Long orderTotalPrice;

    @Column(name = "order_delivery_fee", nullable = false)
    private Long orderDeliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 50)
    private OrderStatus orderStatus;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @OneToOne(mappedBy = "storeOrder", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "storeOrder", fetch = FetchType.LAZY)
    private Review review;

    @OneToOne(mappedBy = "storeOrder", fetch = FetchType.LAZY)
    private OrderDelivery orderDelivery;

    @OneToMany(mappedBy = "storeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>(); // ✅ 단수 -> 복수 권장

    /**
     * 주문 최초 저장(INSERT) 직전에 orderDeliveryFee, orderStatus, 및 orderDate를 자동 세팅합니다.
     * 이미 orderDate가 지정된 경우(외부 입력/특수 케이스)에는 덮어쓰이지 않습니다.
     */
    @PrePersist
    public void prePersist() {
        if (this.orderDeliveryFee == null) this.orderDeliveryFee = 0L;
        if (this.orderStatus == null) this.orderStatus = OrderStatus.PENDING;
        if (this.orderDate == null) this.orderDate = LocalDateTime.now();
    }

    public void changeStatus(OrderStatus nextStatus) {
        this.orderStatus = nextStatus;
    }
}
