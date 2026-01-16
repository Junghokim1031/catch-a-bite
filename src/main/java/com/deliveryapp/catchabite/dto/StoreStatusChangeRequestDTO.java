package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 영업상태 변경 전용 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreStatusChangeRequestDTO {

	@NotNull(message = "storeOpenStatus is required")
	private StoreOpenStatus storeOpenStatus;
}
