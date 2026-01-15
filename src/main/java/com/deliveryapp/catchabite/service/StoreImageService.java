package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.StoreImageDTO;

import java.util.List;

public interface StoreImageService {

	List<StoreImageDTO> getStoreImages(Long storeOwnerId, Long storeId);

	StoreImageDTO createStoreImage(Long storeOwnerId, Long storeId, StoreImageDTO dto);

	void deleteStoreImage(Long storeOwnerId, Long storeId, Long storeImageId);
}
