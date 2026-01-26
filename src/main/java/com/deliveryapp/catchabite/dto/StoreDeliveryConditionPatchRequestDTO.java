package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배달조건 부분 수정(PATCH) 요청 DTO
 * - null인 필드는 변경하지 않습니다.
 * - 최소주문금액/최대배달거리/배달비만 다룹니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreDeliveryConditionPatchRequestDTO {

	@Min(0)
	@Max(2_000_000_000)
	private Integer storeMinOrder;

	@Min(0)
	@Max(1_000_000)
	private Integer storeMaxDist;

	@Min(0)
	@Max(2_000_000_000)
	private Integer storeDeliveryFee;

}
