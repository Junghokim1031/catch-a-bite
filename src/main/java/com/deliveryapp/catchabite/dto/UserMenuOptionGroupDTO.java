package com.deliveryapp.catchabite.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserMenuOptionGroupDTO {
    private Long menuOptionGroupId;
    private String menuOptionGroupName;
    private Boolean required; // true if user MUST select something
    private List<UserMenuOptionDTO> options;
}