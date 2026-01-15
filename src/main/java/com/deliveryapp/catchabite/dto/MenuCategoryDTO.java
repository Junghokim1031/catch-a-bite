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
public class MenuCategoryDTO {

	private Long menuCategoryId;
	private Long storeId;

	// OwnerMenuCategoryController에서 사용
	private String menuCategoryName;
}
