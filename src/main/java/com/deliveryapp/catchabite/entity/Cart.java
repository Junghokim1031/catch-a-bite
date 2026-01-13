package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 엔티티
 * 사용자 + 가게 기준 장바구니
 */
@Entity
@Table(name = "CART")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    // 장바구니 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long cartId;

    // 장바구니 소유 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_USER_ID", nullable = false)
    private AppUser appUser;

    // 장바구니 대상 가게
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    // 장바구니 마지막 수정일
    @Column(name = "CART_UPDATE_AT")
    private LocalDate cartUpdateAt;

    // 장바구니 상품 목록
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();
}
