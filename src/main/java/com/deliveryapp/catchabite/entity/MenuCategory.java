package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MENU_CATEGORY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"store", "menus"})
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_CATEGORY_ID")
    private Long menuCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @Column(name = "MENU_CATEGORY_NAME", nullable = false, length = 100)
    private String menuCategoryName;

    @OneToMany(mappedBy = "menuCategory", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();
}
