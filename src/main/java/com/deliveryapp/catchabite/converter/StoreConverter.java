package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class StoreConverter {

    public StoreDTO toDto(Store store) {
        if (store == null) return null;
        return StoreDTO.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .storePhone(store.getStorePhone())
                .storeAddress(store.getStoreAddress())
                .storeCategory(store.getStoreCategory())
                .storeMinOrder(store.getStoreMinOrder())
                .storeMaxDist(store.getStoreMaxDist())
                .storeDeliveryFee(store.getStoreDeliveryFee())
                .storeOpenTime(store.getStoreOpenTime())
                .storeCloseTime(store.getStoreCloseTime())
                .storeOpenStatus(store.getStoreOpenStatus())
                .storeIntro(store.getStoreIntro())
                .build();
    }

    public StoreSummaryDTO toSummaryDTO(Store store) {
        if (store == null) return null;

        List<String> imageUrls = (store.getImages() != null)
            ? store.getImages().stream()
                .map(StoreImage::getStoreImageUrl)
                .collect(Collectors.toList())
            : Collections.emptyList();

        return StoreSummaryDTO.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .storeOpenStatus(store.getStoreOpenStatus())
                .storeDeliveryFee(store.getStoreDeliveryFee())
                .storeRating(store.getStoreRating())
                .storeImageUrl(imageUrls)
                .storeMinOrderPrice(store.getStoreMinOrder())
                .build();
    }
}