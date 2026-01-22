package com.deliveryapp.catchabite.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMenuOptionDTO {
    private Long menuOptionId;
    private String menuOptionName;
    private Integer menuOptionPrice;
}