package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreDeliveryConditionPatchRequestDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;

import java.util.List;

public interface StoreService {

	StoreDTO getStoreInfo(Long storeOwnerId, Long storeId);

	StoreDTO createStore(Long storeOwnerId, StoreDTO dto);

	StoreDTO updateStoreBasicInfo(Long storeOwnerId, Long storeId, StoreDTO dto);

	/**
	 * 부분 수정용 (PATCH)
	 * - null인 필드는 변경하지 않습니다.
	 */
	StoreDTO patchStoreBasicInfo(Long storeOwnerId, Long storeId, StorePatchRequestDTO dto);

	/**
	 * 배달조건 부분 수정용 (PATCH)
	 * - null인 필드는 변경하지 않습니다.
	 */
	StoreDTO patchStoreDeliveryCondition(Long storeOwnerId, Long storeId, StoreDeliveryConditionPatchRequestDTO dto);

	void changeStoreStatus(Long storeOwnerId, Long storeId, StoreOpenStatus status);

	/**
	 * 상태 변경 요청 DTO를 분리해 프론트/백 스펙이 흔들리지 않도록 합니다.
	 */
	void changeStoreStatus(Long storeOwnerId, Long storeId, StoreStatusChangeRequestDTO req);

	// ✅ 추가: 내 매장 목록(요약) 조회
	List<StoreSummaryDTO> getMyStores(Long storeOwnerId);

}
