package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;

@Entity
@Table(name = "STORE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "images", "menuCategories", "menus"
})
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID")
    private Long storeId;

    @Column(name = "STORE_OWNER_NAME", nullable = false, length = 100)
    private String storeOwnerName;

    @Column(name = "STORE_NAME", nullable = false, length = 100)
    private String storeName;

    @Column(name = "STORE_ADDRESS", nullable = false, length = 400)
    private String storeAddress;

    @Column(name = "STORE_CATEGORY", nullable = false, length = 50)
    private String storeCategory;

    @Column(name = "STORE_PHONE", nullable = false, length = 10)
    private String storePhone;

    @Column(name = "STORE_MIN_ORDER")
    private Integer storeMinOrder;

    @Column(name = "STORE_MAX_DIST")
    private Integer storeMaxDist;

    @Column(name = "STORE_DELIVERY_FEE")
    private Integer storeDeliveryFee;

    @Column(name = "STORE_OPEN_TIME")
    private Integer storeOpenTime;

    @Column(name = "STORE_CLOSE_TIME")
    private Integer storeCloseTime;

    @Column(name = "STORE_RATING")
    private Double storeRating;

    @Column(name = "STORE_TOTAL_ORDER")
    private Integer storeTotalOrder;

    @Column(name = "STORE_RECENT_ORDER")
    private Integer storeRecentOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STORE_OWNER_ID", nullable = false)
    private StoreOwner storeOwner;
  
    // 영업 상태
    // OPEN / CLOSE
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STORE_OPEN_STATUS", length = 10)
    private StoreOpenStatus storeOpenStatus;

    @Column(name = "STORE_INTRO", length = 4000)
    private String storeIntro;

  
    // 비즈니스 메서드
     
   
    // 가게 기본 정보 변경
     
    public void changeInfo(String name, String phone) {
        this.storeName = name;
        this.storePhone = phone;
    }


    // 가게 영업 상태 변경

    public void changeStatus(StoreOpenStatus status) {
        this.storeOpenStatus = status;
    }

    // DTO 기준 가게 정보 수정
    public void update(com.deliveryapp.catchabite.dto.StoreUpdateRequestDto dto) {
        this.storeName = dto.getStoreName();
        this.storePhone = dto.getStorePhone();
        this.storeIntro = dto.getStoreIntro();
    }

    

  
    // 연관관계
     

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<StoreImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuCategory> menuCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();
}
