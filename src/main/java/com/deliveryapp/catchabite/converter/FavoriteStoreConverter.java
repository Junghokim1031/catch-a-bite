package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.FavoriteStore;
import com.deliveryapp.catchabite.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class FavoriteStoreConverter {

    public FavoriteStoreDTO toDto(FavoriteStore entity) {
        if (entity == null) return null;

        return FavoriteStoreDTO.builder()
                .favoriteId(entity.getFavoriteId())
                .appUserId(entity.getAppUser() != null ? entity.getAppUser().getAppUserId() : null)
                .storeId(entity.getStore() != null ? entity.getStore().getStoreId() : null)
                .build();
    }

    public FavoriteStore toEntity(FavoriteStoreDTO dto, AppUser appUser, Store store) {
        if (dto == null) return null;

        return FavoriteStore.builder()
                .favoriteId(dto.getFavoriteId())
                .appUser(appUser)
                .store(store)
                .build();
    }
}