package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.converter.MenuImageConverter;
import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.domain.enumtype.StoreCategory;
import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.dto.MenuImageDTO;
import com.deliveryapp.catchabite.dto.UserMenuImageDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final MenuImageRepository menuImageRepository;
    private final MenuImageConverter menuImageConverter;

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
        // 1. 가게 조회
        Store store = storeRepository.findStoreWithCategoriesById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게id입니다."));

        // 2. 가게 대표 이미지 처리
        String storeImageUrl = null;
        if (store.getImages() != null && !store.getImages().isEmpty()) {
            storeImageUrl = store.getImages().get(0).getStoreImageUrl();
        }

        // 3. 리뷰 수 조회
        Integer reviewCount = (int) reviewRepository.countByStore_StoreId(storeId);

        // 4. 메뉴 이미지 일괄 조회 (N+1 문제 방지 최적화)
        // 4-1. 가게의 모든 메뉴 ID 추출
        List<Long> allMenuIds = store.getMenuCategories().stream()
            .flatMap(cat -> cat.getMenus().stream())
            .map(Menu::getMenuId)
            .collect(Collectors.toList());

        // 4-2. 메뉴 ID로 이미지 조회
        List<MenuImage> menuImages = menuImageRepository.findByMenu_MenuIdIn(allMenuIds);
        
        // 4-3. 조회된 이미지를 Map으로 변환 (Key: MenuId, Value: ImageUrl)
        Map<Long, String> imageMap = menuImages.stream()
            .collect(Collectors.toMap(
                img -> img.getMenu().getMenuId(),
                MenuImage::getMenuImageUrl
            ));

        // =================================================================
        // Step 5. 카테고리 및 메뉴 DTO 변환 (For 반복문 사용)
        // =================================================================
        
        // 1. 결과를 담을 빈 리스트를 만듭니다.
        List<MenuCategoryWithMenusDTO> categoryDTOs = new ArrayList<>();

        // 2. 가게의 모든 카테고리를 하나씩 꺼냅니다.
        for (MenuCategory category : store.getMenuCategories()) {
            
            // 2-1. 해당 카테고리에 속한 메뉴들을 담을 빈 리스트를 만듭니다.
            List<UserMenuImageDTO> menuDtos = new ArrayList<>();

            // 2-2. 카테고리 안의 메뉴들을 하나씩 꺼내서 변환합니다.
            for (Menu menu : category.getMenus()) {
                
                // 아까 만들어둔 지도(Map)에서 메뉴 ID에 맞는 이미지 URL을 가져옵니다.
                String imgUrl = imageMap.get(menu.getMenuId());

                // 메뉴 엔티티를 DTO로 변환합니다.
                UserMenuImageDTO menuDto = menuImageConverter.toDto(
                        menu, 
                        storeId, 
                        category.getMenuCategoryId(), 
                        imgUrl
                );

                // 변환된 메뉴를 리스트에 담습니다.
                menuDtos.add(menuDto);
            }

            // 2-3. 메뉴 리스트가 완성되었으니, 카테고리 DTO를 만듭니다.
            MenuCategoryWithMenusDTO categoryDTO = MenuCategoryWithMenusDTO.builder()
                    .menuCategoryId(category.getMenuCategoryId())
                    .menuCategoryName(category.getMenuCategoryName())
                    .menus(menuDtos) // 완성된 메뉴 리스트 연결
                    .build();

            // 2-4. 최종 리스트에 추가합니다.
            categoryDTOs.add(categoryDTO);
        }


        // =================================================================
        // Step 6. 즐겨찾기 여부 확인 (단계별 IF문 사용)
        // =================================================================
        
        Long favoriteId = null; // 기본값은 null (즐겨찾기 안 함)

        // 1. 로그인한 사용자인지 확인합니다.
        if (userLoginId != null) {
            
            // 2. 사용자 정보를 DB에서 가져옵니다.
            AppUser appUser = appUserRepository.findByAppUserEmail(userLoginId).orElse(null);

            // 3. 사용자가 존재하면, 즐겨찾기 테이블을 조회합니다.
            if (appUser != null) {
                // "내 ID"와 "가게 ID"로 즐겨찾기 데이터를 찾습니다.
                FavoriteStore favorite = favoriteStoreRepository
                        .findByAppUser_AppUserIdAndStore_StoreId(appUser.getAppUserId(), storeId)
                        .orElse(null);

                // 4. 즐겨찾기 데이터가 있으면 ID를 꺼냅니다.
                if (favorite != null) {
                    favoriteId = favorite.getFavoriteId();
                }
            }
        }

        // 7. 최종 응답 DTO 생성
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
