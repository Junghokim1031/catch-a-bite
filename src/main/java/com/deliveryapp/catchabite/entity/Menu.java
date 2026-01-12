package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MENU")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "store", "menuCategory", "optionGroups"
})
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_CATEGORY_ID", nullable = false)
    private MenuCategory menuCategory;

    @Column(name = "MENU_NAME", nullable = false, length = 200)
    private String menuName;

    @Column(name = "MENU_PRICE", nullable = false)
    private Integer menuPrice;

    @Column(name = "MENU_DESCRIPTION", length = 500)
    private String menuDescription;

    @Column(name = "MENU_IS_AVAILABLE", length = 1)
    private String menuIsAvailable;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOptionGroup> optionGroups = new ArrayList<>();
}
