package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;

public interface UserStoreService {
    
	/**
	 * 사용자가 가게명 또는 음식 분류를 타입하여 가게들을 검색할 수 있도록 합니다.
	 */
    public List<StoreDTO> searchStores(String keyword);

    /**
	 * 음식 분류로 가게들을 검색할 수 있도록 합니다.
	 */
    public List<StoreDTO> getStoresByCategory(String storeCategory);

	public List<StoreSummaryDTO> getRandomStores(int limit);
}
