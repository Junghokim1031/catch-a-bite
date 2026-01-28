package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.MenuImageDTO;
import com.deliveryapp.catchabite.entity.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuImageConverter {

    public MenuImageDTO toDto(Menu menu, Long storeId, Long menuCategoryId, String imageUrl) {
        if (menu == null) return null;

        return MenuImageDTO.builder()
                .menuId(menu.getMenuId())
                .storeId(storeId)
                .menuCategoryId(menuCategoryId)
                .menuName(menu.getMenuName())
                .menuDescription(menu.getMenuDescription())
                .menuPrice(menu.getMenuPrice())
                .menuIsAvailable(menu.getMenuIsAvailable())
                .menuImageUrl(imageUrl) // 이미지 URL 설정
                .build();
    }
}