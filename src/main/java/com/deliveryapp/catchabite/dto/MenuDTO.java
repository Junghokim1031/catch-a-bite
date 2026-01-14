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
public class MenuDTO {

	private Long menuId;
	private Long storeId;
	private Long menuCategoryId;

	// OwnerMenuController에서 사용
	private String menuName;
	private String menuDescription;
	private Integer menuPrice;

	/**
	 * boolean 네이밍 규칙 반영 (isAvailable 계열)
	 * 기존 엔티티/컨트롤러 getter 패턴(menuIsAvailable)과도 자연스럽게 맞습니다.
	 */
	private Boolean menuIsAvailable;
}
