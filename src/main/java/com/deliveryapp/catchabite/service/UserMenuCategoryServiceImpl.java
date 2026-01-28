package com.deliveryapp.catchabite.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.MenuImageConverter;
import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.MenuImageDTO;
import com.deliveryapp.catchabite.dto.UserMenuDetailDTO;
import com.deliveryapp.catchabite.dto.UserMenuOptionDTO;
import com.deliveryapp.catchabite.dto.UserMenuOptionGroupDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.entity.MenuImage;
import com.deliveryapp.catchabite.repository.MenuCategoryRepository;
import com.deliveryapp.catchabite.repository.MenuImageRepository;
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
    private final MenuImageRepository menuImageRepository;
    private final MenuImageConverter menuImageConverter;
    

    @Override
    public UserMenuDetailDTO getMenuDetail(Long menuId) {
        // Fetch Join을 사용하는 메서드로 교체하여 N+1 문제 해결
        Menu menu = menuRepository.findMenuDetailById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        String menuImageUrl = menuImageRepository.findByMenu_MenuId(menuId)
                .map(MenuImage::getMenuImageUrl)
                .orElse(null);

        return UserMenuDetailDTO.builder()
                .menuId(menu.getMenuId())
                .menuName(menu.getMenuName())
                .menuDescription(menu.getMenuDescription())
                .menuPrice(menu.getMenuPrice())
                .menuImageUrl(menuImageUrl)
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
        List<MenuCategory> categories = menuCategoryRepository.findAllWithMenusByStoreId(storeId);

        // 3. 이미지 일괄 조회 (N+1 문제 해결)
        // 모든 메뉴 ID 추출
        List<Long> allMenuIds = categories.stream()
                .flatMap(cat -> cat.getMenus().stream())
                .map(Menu::getMenuId)
                .collect(Collectors.toList());
        // 이미지 조회 및 Map 변환
        List<MenuImage> images = menuImageRepository.findByMenu_MenuIdIn(allMenuIds);
        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        img -> img.getMenu().getMenuId(),
                        MenuImage::getMenuImageUrl
                ));

        // 4. DTO 변환 (이미지 맵 전달)
        List<MenuCategoryWithMenusDTO> result = new ArrayList<>();

        for (MenuCategory category : categories) {
            List<MenuImageDTO> menuDtos = new ArrayList<>();

            for (Menu menu : category.getMenus()) {
                String imgUrl = imageMap.get(menu.getMenuId());
                
                // [CHANGED] Use MenuImageConverter to get MenuImageDTO
                MenuImageDTO dto = menuImageConverter.toDto(
                        menu, 
                        storeId, 
                        category.getMenuCategoryId(), 
                        imgUrl
                );
                menuDtos.add(dto);
            }

            result.add(MenuCategoryWithMenusDTO.builder()
                    .menuCategoryId(category.getMenuCategoryId())
                    .menuCategoryName(category.getMenuCategoryName())
                    .menus(menuDtos) // This matches the updated DTO definition
                    .build());
        }
        return result;
    }
}
