package com.deliveryapp.catchabite.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserMenuDetailDTO {
    // Basic Menu Info
    private Long menuId;
    private String menuName;
    private String menuDescription;
    private Integer menuPrice;
    private String menuImageUrl; // Assuming you have this or will add it
    private Boolean menuIsAvailable;

    // Hierarchy
    private List<UserMenuOptionGroupDTO> optionGroups;
}