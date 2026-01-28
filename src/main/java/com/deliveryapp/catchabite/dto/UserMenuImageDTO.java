package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMenuImageDTO {
    
    private Long menuId;
    private Long storeId;
    private Long menuCategoryId;

    private String menuName;
    private String menuDescription;
    private Integer menuPrice;
    private Boolean menuIsAvailable;

    private String menuImageUrl;
}
