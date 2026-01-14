package com.deliveryapp.catchabite.entity;

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
@Table(name = "STORE_ORDER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APP_USER_ID", nullable = false) // ERD shows nullable
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private Address address;

    @Column(name = "ORDER_ADDRESS_SNAPSHOT", nullable = false, length = 255)
    private String orderAddressSnapshot;

    @Column(name = "ORDER_TOTAL_PRICE", nullable = false)
    private Integer orderTotalPrice;

    @Column(name = "ORDER_DELIVERY_FEE", nullable = false)
    private Integer orderDeliveryFee;

    @Column(name = "ORDER_STATUS", nullable = false, length = 50)
    private String orderStatus;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @OneToOne(mappedBy = "storeOrder", fetch = FetchType.EAGER)
    private Payment payment;

    // @OneToOne(mappedBy = "storeOrder", fetch = FetchType.LAZY)
    // private Review review;

    @OneToOne(mappedBy = "storeOrder", fetch = FetchType.EAGER)
    private OrderDelivery orderDelivery;

    @OneToMany(mappedBy = "storeOrder", cascade=CascadeType.ALL)
	@Builder.Default
    private List<OrderItem> orderItem = new ArrayList<>();

    /**
     * 주문 최초 저장(INSERT) 직전에 orderDate를 자동 세팅합니다.
     * 이미 orderDate가 지정된 경우(외부 입력/특수 케이스)에는 덮어쓰이지 않습니다.
     */
    @PrePersist
    private void prePersist() {
        if (orderDate == null) orderDate = LocalDateTime.now();
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
