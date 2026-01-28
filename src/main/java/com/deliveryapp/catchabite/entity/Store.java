package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import com.deliveryapp.catchabite.domain.enumtype.StoreCategory;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "storeOwner", "images", "menuCategories", "menus"
})
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_owner_name", nullable = false, length = 100)
    private String storeOwnerName;

    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Column(name = "store_address", nullable = false, length = 400)
    private String storeAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_category", nullable = false, length = 50)
    private StoreCategory storeCategory;


    @Column(name = "store_phone", nullable = false, length = 10)
    private String storePhone;

    @Column(name = "store_min_order")
    private Integer storeMinOrder;

    @Column(name = "store_max_dist")
    private Integer storeMaxDist;

    @Column(name = "store_delivery_fee")
    private Integer storeDeliveryFee;

    @Column(name = "store_open_time")
    private Integer storeOpenTime;

    @Column(name = "store_close_time")
    private Integer storeCloseTime;

    @Column(name = "store_rating")
    private Double storeRating;

    @Column(name = "store_total_order")
    private Integer storeTotalOrder;

    @Column(name = "store_recent_order")
    private Integer storeRecentOrder;

    // 영업 상태 (OPEN / CLOSE)
    @Enumerated(EnumType.STRING)
    @Column(name = "store_open_status", length = 10)
    private StoreOpenStatus storeOpenStatus;

    @Column(name = "store_intro", length = 4000)
    private String storeIntro;

    // 원산지 표기(텍스트)
    @Column(name = "store_origin_label", length = 4000)
    private String storeOriginLabel;

    /* =========================
       비즈니스 메서드
       ========================= */

    // 가게 영업 상태 변경
    public void changeStatus(StoreOpenStatus status) {
        this.storeOpenStatus = status;
    }

    // 가게 기본 정보 변경
    public void changeBasicInfo(String storeName, String storePhone, String storeIntro){
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.storeIntro = storeIntro;
    }

    // 가게 기본 정보 "전체" 변경 (PUT용)
    public void changeStoreInfo(String storeName, String storePhone, String storeAddress, StoreCategory storeCategory, String storeIntro) {
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.storeAddress = storeAddress;
        this.storeCategory = storeCategory;
        this.storeIntro = storeIntro;
    }


    public void changeDeliveryCondition(Integer storeMinOrder, Integer storeMaxDist, Integer storeDeliveryFee) {
        this.storeMinOrder = storeMinOrder;
        this.storeMaxDist = storeMaxDist;
        this.storeDeliveryFee = storeDeliveryFee;
    }

    public void changeOwnerSnapshotName(String ownerName) {
        this.storeOwnerName = ownerName;
    }

    public void changeOriginLabel(String originLabel) {
        this.storeOriginLabel = originLabel;
    }



    /* =========================
       연관관계
       ========================= */

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<StoreImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuCategory> menuCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_owner_id", nullable = true)
    private StoreOwner storeOwner;

}
