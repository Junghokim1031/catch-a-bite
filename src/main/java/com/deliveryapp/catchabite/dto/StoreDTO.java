package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreDTO {

	private Long storeId;

	private String storeName;
	private String storePhone;
	private String storeAddress;
	private String storeCategory;

	private Integer storeMinOrder;
	private Integer storeMaxDist;  
	private Integer storeDeliveryFee;

	private Integer storeOpenTime;
	private Integer storeCloseTime;

	private StoreOpenStatus storeOpenStatus;

	private String storeIntro;



}
