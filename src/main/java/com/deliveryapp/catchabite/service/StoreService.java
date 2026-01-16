package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;

public interface StoreService {

	StoreDTO getStoreInfo(Long storeOwnerId, Long storeId);

	StoreDTO createStore(Long storeOwnerId, StoreDTO dto);

	StoreDTO updateStoreBasicInfo(Long storeOwnerId, Long storeId, StoreDTO dto);

	/**
	 * 부분 수정용 (PATCH)
	 * - null인 필드는 변경하지 않습니다.
	 */
	StoreDTO patchStoreBasicInfo(Long storeOwnerId, Long storeId, StorePatchRequestDTO dto);

	void changeStoreStatus(Long storeOwnerId, Long storeId, StoreOpenStatus status);

	/**
	 * 상태 변경 요청 DTO를 분리해 프론트/백 스펙이 흔들리지 않도록 합니다.
	 */
	void changeStoreStatus(Long storeOwnerId, Long storeId, StoreStatusChangeRequestDTO req);

}
