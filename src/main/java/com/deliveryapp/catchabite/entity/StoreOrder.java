package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "STORE_ORDER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_USER_ID") // ERD shows nullable
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Review review;

    @OneToOne(mappedBy = "orderId", fetch = FetchType.LAZY)
    private OrderDelivery orderDelivery;

    @OneToMany(mappedBy = "orderId", cascade=CascadeType.ALL)
    private Set<OrderItem> orderItem;
}

