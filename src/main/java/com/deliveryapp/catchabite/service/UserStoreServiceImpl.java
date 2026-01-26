/* catchabite/service/UserStoreServiceImpl.java */
package com.deliveryapp.catchabite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.dto.UserStoreResponseDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.FavoriteStore;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.FavoriteStoreRepository;
import com.deliveryapp.catchabite.repository.ReviewRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoreServiceImpl implements UserStoreService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final ReviewRepository reviewRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;
    private final AppUserRepository appUserRepository;
    
    @Override    
    public List<StoreSummaryDTO> searchStores(String keyword) {
        List<Store> stores = storeRepository.findByStoreNameContainingIgnoreCaseOrStoreCategoryContainingIgnoreCase(keyword, keyword);
        return stores.stream()
                .map(storeConverter::toSummaryDTO)
                .toList();
    }

    @Override
    public List<StoreSummaryDTO> getStoresByCategory(String storeCategory) {
        List<Store> stores = storeRepository.findByStoreCategory(storeCategory);
        return stores.stream()
                .map(storeConverter::toSummaryDTO)
                .toList();
    }

    @Override
    public List<StoreSummaryDTO> getRandomStores() {
        List<Store> allOpenStores = storeRepository.findByStoreOpenStatus(StoreOpenStatus.OPEN);
        return allOpenStores.stream()
                .map(storeConverter::toSummaryDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserStoreResponseDTO getStoreDetailsForUser(Long storeId, String userLoginId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게id입니다."));

        String storeImageUrl = null;
        if (store.getImages() != null && !store.getImages().isEmpty()) {
            storeImageUrl = store.getImages().get(0).getStoreImageUrl();
        }

        Integer reviewCount = (int) reviewRepository.countByStore_StoreId(storeId);

        List<MenuCategoryWithMenusDTO> categoryDTOs = store.getMenuCategories().stream()
                .map(category -> MenuCategoryWithMenusDTO.builder()
                        .menuCategoryId(category.getMenuCategoryId())
                        .menuCategoryName(category.getMenuCategoryName())
                        .menus(category.getMenus().stream()
                                .map(menu -> MenuDTO.builder()
                                        .menuId(menu.getMenuId())
                                        .menuName(menu.getMenuName())
                                        .menuPrice(menu.getMenuPrice())
                                        .menuDescription(menu.getMenuDescription())
                                        .menuIsAvailable(menu.getMenuIsAvailable())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        Long favoriteId = null;
        if (userLoginId != null) {
            AppUser appUser = appUserRepository.findByAppUserEmail(userLoginId).orElse(null);
            
            if (appUser != null) {
                favoriteId = favoriteStoreRepository.findByAppUser_AppUserIdAndStore_StoreId(appUser.getAppUserId(), storeId)
                        .map(FavoriteStore::getFavoriteId)
                        .orElse(null);
            }
        }

        return UserStoreResponseDTO.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .storeImageUrl(storeImageUrl)
                .rating(store.getStoreRating())
                .reviewCount(reviewCount)
                .storeIntro(store.getStoreIntro())
                .storePhone(store.getStorePhone())
                .storeAddress(store.getStoreAddress())
                .storeCategory(store.getStoreCategory())
                .storeOpenStatus(store.getStoreOpenStatus())
                .minOrderPrice(store.getStoreMinOrder())
                .deliveryFee(store.getStoreDeliveryFee())
                .estimatedDeliveryTime("20-30분")
                .menuCategories(categoryDTOs)
                .favoriteId(favoriteId) 
                .build();
    }
}