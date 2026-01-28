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

    /**
     * 메뉴 대표 이미지(썸네일) URL
     * - 목록 조회 성능을 위해 menu 테이블에 직접 보관(denormalize)
     * - 추가 이미지는 menu_image 테이블에서 관리
     */
    @Column(name = "menu_thumbnail_url")
    private String menuThumbnailUrl;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOptionGroup> menuOptionGroups = new ArrayList<>();

    @OneToOne(mappedBy = "menu", fetch = FetchType.LAZY)
    private MenuImage menuImage;

    public void changeInfo(MenuCategory category, String name, Integer price, String description) {
        this.menuCategory = category;
        this.menuName = name;
        this.menuPrice = price;
        this.menuDescription = description;
    }

    public void changeAvailability(Boolean isAvailable) {
        this.menuIsAvailable = isAvailable;
    }

    public void changeThumbnailUrl(String thumbnailUrl) {
        this.menuThumbnailUrl = thumbnailUrl;
    }


}
