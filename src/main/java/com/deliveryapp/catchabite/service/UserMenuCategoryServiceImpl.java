package com.deliveryapp.catchabite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.MenuConverter;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.dto.UserMenuDetailDTO;
import com.deliveryapp.catchabite.dto.UserMenuOptionDTO;
import com.deliveryapp.catchabite.dto.UserMenuOptionGroupDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.repository.MenuCategoryRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMenuCategoryServiceImpl implements UserMenuCategoryService{

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuConverter menuConverter;
    

    @Override
    public UserMenuDetailDTO getMenuDetail(Long menuId) {
        // Fetch Menu with OptionGroups and Options to avoid N+1
        // Note: You might need a custom query in MenuRepository for FETCH JOIN
        // For now, relying on Lazy loading with BatchSize (if configured) or standard fetching
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        return UserMenuDetailDTO.builder()
                .menuId(menu.getMenuId())
                .menuName(menu.getMenuName())
                .menuDescription(menu.getMenuDescription())
                .menuPrice(menu.getMenuPrice())
                .menuIsAvailable(menu.getMenuIsAvailable())
                // Map Option Groups
                .optionGroups(menu.getMenuOptionGroups().stream()
                        .map(group -> UserMenuOptionGroupDTO.builder()
                                .menuOptionGroupId(group.getMenuOptionGroupId())
                                .menuOptionGroupName(group.getMenuOptionGroupName())
                                .required(group.getMenuOptionGroupRequired())
                                // Map Options
                                .options(group.getMenuOptions().stream()
                                        .map(option -> UserMenuOptionDTO.builder()
                                                .menuOptionId(option.getMenuOptionId())
                                                .menuOptionName(option.getMenuOptionName())
                                                .menuOptionPrice(option.getMenuOptionPrice())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }


    @SuppressWarnings("null")
    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryWithMenusDTO> getMenuBoardForUser(Long storeId) {
            // 1. 매장 존재 여부만 확인
            if (!storeRepository.existsById(storeId)) {
                    throw new IllegalArgumentException("존재하지 않는 매장입니다. storeId=" + storeId);
            }

            // 2. 해당 매장의 카테고리 목록 조회
            List<MenuCategory> categories = menuCategoryRepository.findAllByStore_StoreId(storeId);

            // 3. DTO 변환 (이전 단계에서 만든 컨버터 또는 변환 로직 사용)
            return categories.stream()
                            .map(this::convertToCategoryWithMenusDto)
                            .toList();
    }

	/**
     * 카테고리별 메뉴 리스트를 포함한 DTO로 변환하는 헬퍼 메서드
     */
    private MenuCategoryWithMenusDTO convertToCategoryWithMenusDto(MenuCategory category) {
        // 해당 카테고리에 속한 메뉴들을 먼저 변환 (MenuConverter 활용)
        List<MenuDTO> menuList = category.getMenus().stream()
                .map(menu -> menuConverter.toDto(
                        menu, 
                        category.getStore().getStoreId(), 
                        category.getMenuCategoryId()))
                .toList();

        // 최종 DTO 생성
        return MenuCategoryWithMenusDTO.builder()
                .menuCategoryId(category.getMenuCategoryId())
                .menuCategoryName(category.getMenuCategoryName())
                .menus(menuList)
                .build();
    }
}
