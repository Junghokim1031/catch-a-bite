package com.deliveryapp.catchabite.dto;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryWithMenusDTO {
    private Long menuCategoryId;          // 메뉴 카테고리 ID
    private String menuCategoryName;      // 카테고리 이름
    private List<MenuImageDTO> menus;     // 해당 카테고리에 속한 메뉴 리스트
}