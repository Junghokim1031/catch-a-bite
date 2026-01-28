package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.FavoriteStoreConverter;
import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;
import com.deliveryapp.catchabite.dto.UserFavoriteStoreResponseDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.FavoriteStore;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.FavoriteStoreRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class FavoriteStoreServiceImpl implements FavoriteStoreService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final AppUserRepository appUserRepository;
    private final StoreRepository storeRepository;
    private final FavoriteStoreConverter favoriteStoreConverter;

    @Override
    @Transactional
    public FavoriteStoreDTO addFavorite(Long storeId, String userLoginId) {
        if (storeId == null || userLoginId == null) {
            throw new IllegalArgumentException("Store ID and User ID are required");
        }
        
        // UPDATED: Use findByAppUserEmail
        AppUser appUser = appUserRepository.findByAppUserEmail(userLoginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Long userId = appUser.getAppUserId();

        // 2. Check Duplicates
        if (favoriteStoreRepository.existsByAppUser_AppUserIdAndStore_StoreId(userId, storeId)) {
            throw new IllegalArgumentException("이미 즐겨찾기에 등록된 가게입니다.");
        }

        // 3. Resolve Store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        // 4. Save
        FavoriteStoreDTO dto = FavoriteStoreDTO.builder()
                .appUserId(userId)
                .storeId(storeId)
                .build();
                
        FavoriteStore entity = favoriteStoreConverter.toEntity(dto, appUser, store);
        @SuppressWarnings("null")
        FavoriteStore saved = favoriteStoreRepository.save(entity);
        return favoriteStoreConverter.toDto(saved);
    }

    @Override
    public List<UserFavoriteStoreResponseDTO> getMyFavoriteStores(String userLoginId) {
        // UPDATED: Use findByAppUserEmail
        AppUser appUser = appUserRepository.findByAppUserEmail(userLoginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        List<FavoriteStore> favorites = favoriteStoreRepository.findAllByAppUserId(appUser.getAppUserId());

        return favorites.stream()
                .map(fs -> {
                    Store store = fs.getStore();
                    return new UserFavoriteStoreResponseDTO(
                            fs.getFavoriteId(),
                            store.getStoreId(),
                            store.getStoreName(),
                            store.getStoreRating()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFavorite(Long favoriteId) {
        if (favoriteId == null) return;
        if (!favoriteStoreRepository.existsById(favoriteId)) {
            throw new IllegalArgumentException("Favorite not found");
        }
        favoriteStoreRepository.deleteById(favoriteId);
    }
}