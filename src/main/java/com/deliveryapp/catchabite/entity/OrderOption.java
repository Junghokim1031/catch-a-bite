package com.deliveryapp.catchabite.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_option")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id", nullable = false)
    private Long orderOptionId;

    // ERD: ORDER_ITEM_ID (FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    // ERD: ORDER_OPTION_NAME varchar2(100) (NN)
    @Column(name = "order_option_name", length = 100, nullable = false)
    private String orderOptionName;

    @Column(name = "order_option_extra_price")
    private Long orderOptionExtraPrice;
}
