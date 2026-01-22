package com.deliveryapp.catchabite.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id", nullable = false)
    private MenuCategory menuCategory;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_price", nullable = false)
    private Integer menuPrice;

    @Column(name = "menu_description")
    private String menuDescription;

    @Column(name = "menu_is_available", nullable = false)
    private Boolean menuIsAvailable;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOptionGroup> menuOptionGroups = new ArrayList<>();

    public void changeInfo(MenuCategory category, String name, Integer price, String description) {
        this.menuCategory = category;
        this.menuName = name;
        this.menuPrice = price;
        this.menuDescription = description;
    }

    public void changeAvailability(Boolean isAvailable) {
        this.menuIsAvailable = isAvailable;
    }


}
