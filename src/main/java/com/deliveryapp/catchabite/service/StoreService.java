package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;

public interface StoreService {

	StoreDTO getStoreInfo(Long storeOwnerId, Long storeId);

	StoreDTO createStore(Long storeOwnerId, StoreDTO dto);

	StoreDTO updateStoreBasicInfo(Long storeOwnerId, Long storeId, StoreDTO dto);

	void changeStoreStatus(Long storeOwnerId, Long storeId, StoreOpenStatus status);

}
