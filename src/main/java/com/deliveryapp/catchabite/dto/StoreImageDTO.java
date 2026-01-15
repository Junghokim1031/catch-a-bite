package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreImageDTO {

	private Long storeImageId;
	private Long storeId;

	/**
	 * 업로드는 별도 처리
	 * DB에는 URL만 저장
	 */
	private String storeImageUrl;
}
