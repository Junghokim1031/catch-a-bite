package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.StoreCategory;
import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreDTO {

	private Long storeId;

	@NotBlank(message = "storeName is required")
	@Size(max = 100, message = "storeName is too long")
	private String storeName;

	@NotBlank(message = "storePhone is required")
	@Size(max = 10, message = "storePhone is too long")
	private String storePhone;

	@NotBlank(message = "storeAddress is required")
	@Size(max = 400, message = "storeAddress is too long")
	private String storeAddress;

	@NotBlank(message = "storeCategory is required")
	@Size(max = 50, message = "storeCategory is too long")
	private String storeCategory;

	private Integer storeMinOrder;
	private Integer storeMaxDist;
	private Integer storeDeliveryFee;

	private Integer storeOpenTime;
	private Integer storeCloseTime;

	private StoreOpenStatus storeOpenStatus;

	@Size(max = 4000, message = "storeIntro is too long")
	private String storeIntro;
}
