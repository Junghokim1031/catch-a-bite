package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOptionGroupDTO {

	private Long menuOptionGroupId;
	private Long menuId;

	// OwnerMenuOptionController에서 사용
	private String menuOptionGroupName;

	/**
	 * 기존 코드에서 required / getRequired() 형태가 함께 보이므로
	 * DTO는 boolean 규칙에 맞춰 isRequired로 통일합니다.
	 */
	private Boolean required;
}
