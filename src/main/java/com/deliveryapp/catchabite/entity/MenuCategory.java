package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id", nullable = false)
    private Long menuCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "menu_category_name")
    private String menuCategoryName;
}
