package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoreResponseDTO {

    // --- Header & Basic Info ---
    private Long storeId;
    private String storeName;
    private String storeImageUrl; // Fetched from StoreImage table
    private Double rating;        // storeRating from Store entity
    private Integer reviewCount;  // Count from ReviewRepository

    // --- Details ---
    private String storeIntro;
    private String storeOriginLabel;
    private String storePhone;
    private String storeAddress;
    private String storeCategory;
    private StoreOpenStatus storeOpenStatus; 
    
    // --- Delivery Logic ---
    private Integer storeOpenTime;
    private Integer storeCloseTime;
    private Integer minOrderPrice;
    private Integer deliveryFee;
    private String estimatedDeliveryTime; // Placeholder (e.g. "30-45분")

    // --- Menu List ---
    private List<MenuCategoryWithMenusDTO> menuCategories;
    
    // --- Favorite Status ---
    private Long favoriteId;
}