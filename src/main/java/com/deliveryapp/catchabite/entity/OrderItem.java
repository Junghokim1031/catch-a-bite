package com.deliveryapp.catchabite.entity;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    // ERD: ORDER_ID (NN)
    // order_id는 storeOrder.getStoreOrderID()로 꺼냄
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private StoreOrder storeOrder;

    // ERD: ORDER_ITEM_NAME varchar2(100) (NN)
    @Column(name = "order_item_name", length = 100, nullable = false)
    private String orderItemName;

    // ERD: ORDER_ITEM_PRICE (NN)
    @Column(name = "order_item_price", nullable = false)
    private Long orderItemPrice;

    // ERD: ORDER_ITEM_QUANTITY (NN)
    @Column(name = "order_item_quantity", nullable = false)
    private Long orderItemQuantity;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderOption> orderOptions = new ArrayList<>();

    /** 연관관계 편의 메서드 */
    // 주문 추가 메서드
    public void addOrderOption(OrderOption option) {
        orderOptions.add(option);
        option.setOrderItem(this);
    }

    // 주문 삭제 메서드
    public void removeOrderOption(OrderOption option) {
        orderOptions.remove(option);
        option.setOrderItem(null);
    }
}