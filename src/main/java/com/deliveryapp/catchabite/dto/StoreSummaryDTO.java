package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreSummaryDTO {

	private Long storeId;
	private String storeName;
	private String storeCategory;
	private String storeAddress;
	private StoreOpenStatus storeOpenStatus;

}
