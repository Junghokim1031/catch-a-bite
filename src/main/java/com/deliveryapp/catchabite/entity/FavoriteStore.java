package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 즐겨찾기 가게 엔티티
 * 사용자와 가게 간 즐겨찾기 매핑
 */
@Entity
@Table(name = "FAVORITE_STORE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteStore {

    // 즐겨찾기 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAVORITE_ID")
    private Long favoriteId;

    // 즐겨찾기 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_USER_ID", nullable = false)
    private AppUser appUser;

    // 즐겨찾기 가게
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;
}
