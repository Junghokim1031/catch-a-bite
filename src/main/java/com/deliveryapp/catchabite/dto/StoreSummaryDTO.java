package com.deliveryapp.catchabite.dto;

import java.util.List;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreSummaryDTO {
   private Long storeId;
   private String storeName;
   private StoreOpenStatus storeOpenStatus;
   private Integer storeDeliveryFee;
   private Double storeRating;
   private List<String> storeImageUrl;
   private Integer storeMinOrderPrice;
}