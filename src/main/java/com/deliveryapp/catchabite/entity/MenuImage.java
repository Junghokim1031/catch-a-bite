package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 메뉴 이미지
 * - 실무에서는 대표 이미지(썸네일) 1장을 필수로 두고,
 *   추가 이미지는 선택적으로 여러 장을 운영하는 경우가 많습니다.
 */
@Entity
@Table(name = "menu_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MenuImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_image_id", nullable = false)
    private Long menuImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "menu_image_url", nullable = false)
    private String menuImageUrl;

    @Column(name = "menu_image_is_main", nullable = false)
    private Boolean menuImageIsMain;

    public void changeMain(Boolean isMain) {
        this.menuImageIsMain = isMain;
    }
}
