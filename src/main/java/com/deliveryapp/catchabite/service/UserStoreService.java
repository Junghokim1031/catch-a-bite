package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.dto.UserStoreResponseDTO;

public interface UserStoreService {
    
	/**
	 * 사용자가 가게명 또는 음식 분류를 타입하여 가게들을 검색할 수 있도록 합니다.
	 */
    public List<UserStoreSummaryDTO> searchStores(String keyword);

    /**
	 * 음식 분류로 가게들을 검색할 수 있도록 합니다.
	 */
    public List<UserStoreSummaryDTO> getStoresByCategory(String storeCategory);

	public List<UserStoreSummaryDTO> getRandomStores();

	/**
     * Fetches store details, including images, rating, and categorized menus for the user view.
     * @param storeId The ID of the store
     * @return UserStoreResponseDTO containing all page data
     */
    UserStoreResponseDTO getStoreDetailsForUser(Long storeId, String userLoginId);
}
