package com.deliveryapp.catchabite.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.MenuConverter;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.repository.MenuCategoryRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMenuCategoryServiceImpl implements UserMenuCategoryService{

    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuConverter menuConverter;
    
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
