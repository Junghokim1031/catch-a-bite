package com.deliveryapp.catchabite.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreUpdateRequestDto {

	private String storeName;
	private String storePhone;

	private Integer storeMinOrder;
	private Integer storeDeliveryFee;
	private Integer storeOpenTime;
	private Integer storeCloseTime;

	private String storeIntro;
}
