package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreDeliveryConditionPatchRequestDTO;
import com.deliveryapp.catchabite.dto.OwnerBusinessInfoDTO;
import com.deliveryapp.catchabite.dto.OwnerBusinessInfoPatchRequestDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.dto.StoreOriginLabelDTO;

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

	// ✅ 추가: 사업자 정보(대표자/상호/주소/사업자번호)
	OwnerBusinessInfoDTO getBusinessInfo(Long storeOwnerId, Long storeId);
	OwnerBusinessInfoDTO patchBusinessInfo(Long storeOwnerId, Long storeId, OwnerBusinessInfoPatchRequestDTO dto);

	// ✅ 추가: 원산지 표기(텍스트)
	StoreOriginLabelDTO getOriginLabel(Long storeOwnerId, Long storeId);
	StoreOriginLabelDTO patchOriginLabel(Long storeOwnerId, Long storeId, StoreOriginLabelDTO dto);

}
