package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 매장 기본정보 부분 수정(PATCH) 요청 DTO
 * - null인 필드는 변경하지 않습니다.
 * - NotBlank 같은 "필수" 제약은 걸지 않습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StorePatchRequestDTO {

	@Size(max = 100, message = "storeName is too long")
	private String storeName;

	@Size(max = 10, message = "storePhone is too long")
	private String storePhone;

	@Size(max = 400, message = "storeAddress is too long")
	private String storeAddress;

	@Size(max = 50, message = "storeCategory is too long")
	private String storeCategory;

	@Size(max = 4000, message = "storeIntro is too long")
	private String storeIntro;
}
