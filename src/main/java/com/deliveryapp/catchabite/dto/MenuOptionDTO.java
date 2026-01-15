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
public class MenuOptionDTO {

	private Long menuOptionId;
	private Long menuOptionGroupId;

	// OwnerMenuOptionController에서 사용
	private String menuOptionName;
	private Integer menuOptionPrice;
}
