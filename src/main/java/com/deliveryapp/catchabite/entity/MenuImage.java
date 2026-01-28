package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MenuImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_img_id")
    private Long menuImgId;

    // Unidirectional OneToOne to Menu
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, unique = true)
    private Menu menu;

    @Column(name = "menu_image_url", nullable = false)
    private String menuImageUrl;

    // Method to update image URL
    public void updateUrl(String newUrl) {
        this.menuImageUrl = newUrl;
    }
}