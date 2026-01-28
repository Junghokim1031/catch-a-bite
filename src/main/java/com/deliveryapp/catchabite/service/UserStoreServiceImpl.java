package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.domain.enumtype.StoreCategory; // ✅ 추가
import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.dto.UserStoreResponseDTO;
import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.FavoriteStore;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.entity.MenuImage;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.FavoriteStoreRepository;
import com.deliveryapp.catchabite.repository.MenuImageRepository;
import com.deliveryapp.catchabite.repository.ReviewRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
	public List<UserStoreSummaryDTO> searchStores(String keyword) {
		// ✅ 원본은 store_category "contains" 검색이었는데,
		// enum 전환 후에는 keyword가 카테고리 값과 일치할 때만 카테고리 필터로 동작하게 처리합니다.

		List<Store> byName = storeRepository.findByStoreNameContainingIgnoreCase(keyword);

		StoreCategory category = null;
		try {
			category = StoreCategory.from(keyword);
		} catch (IllegalArgumentException ignored) {
			// keyword가 카테고리 값이 아니면 카테고리 검색은 건너뜀
		}

		List<Store> result;
		if (category == null) {
			result = byName;
		} else {
			// 이름 검색 + 카테고리 검색 결과를 합치되 중복 제거
			List<Store> byCategory = storeRepository.findByStoreCategory(category);
			result = new java.util.ArrayList<>(byName);
			for (Store s : byCategory) {
				if (!result.contains(s)) {
					result.add(s);
				}
			}
		}

		return result.stream()
				.map(storeConverter::toSummaryDTO)
				.toList();
	}

	@Override
	public List<UserStoreSummaryDTO> getStoresByCategory(String storeCategory) {
		StoreCategory category = StoreCategory.from(storeCategory);
		List<Store> stores = storeRepository.findByStoreCategory(category);

		return stores.stream()
				.map(storeConverter::toSummaryDTO)
				.toList();
	}

	@Override
	public List<UserStoreSummaryDTO> getRandomStores() {
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
				favoriteId = favoriteStoreRepository
						.findByAppUser_AppUserIdAndStore_StoreId(appUser.getAppUserId(), storeId)
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
				.storeCategory(store.getStoreCategory().name())
				.storeOpenStatus(store.getStoreOpenStatus())
				.minOrderPrice(store.getStoreMinOrder())
				.deliveryFee(store.getStoreDeliveryFee())
				.estimatedDeliveryTime("20-30분")
				.menuCategories(categoryDTOs)
				.favoriteId(favoriteId)
				.build();
	}
}
