package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "CART_ITEM")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cartId","menuId"})
public class CartItem {
    @Id
    @Column(name="CART_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CART_ID", nullable = false)
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="MENU_ID", nullable = false)
    private Menu menu;

    @Column(name="CART_ITEM_QUANTITY", nullable = false)
    private Integer cartItemQuantity;
}
