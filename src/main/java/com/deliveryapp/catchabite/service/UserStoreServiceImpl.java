package com.deliveryapp.catchabite.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.StoreConverter;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoreServiceImpl implements UserStoreService {

    private final StoreRepository storeRepository;
	private final StoreConverter storeConverter;

    //사용자가 가게를 검색하는대 사용됨
	@Override    
    public List<StoreDTO> searchStores(String keyword) {
        // StoreName과 StoreCategory를 키워드로 검색
        List<Store> stores = storeRepository.findByStoreNameContainingIgnoreCaseOrStoreCategoryContainingIgnoreCase(keyword, keyword);
        
        // storeConverter를 사용하여 엔티티를 DTO로 변환
        return stores.stream()
                .map(storeConverter::toDto)
                .toList();
    }

	//가게를 store.storeCategory로 검색함.
    @Override
    public List<StoreDTO> getStoresByCategory(String storeCategory) {
        // 특정 음식 카테고리로 매장 검색
        List<Store> stores = storeRepository.findByStoreCategory(storeCategory);
        
        // storeConverter를 사용하여 엔티티를 DTO로 변환
        return stores.stream()
                .map(storeConverter::toDto)
                .toList();
    }

    @Override
    public List<StoreSummaryDTO> getRandomStores(int limit) {
        List<Store> stores = storeRepository.findRandomStores(limit);
        List<StoreSummaryDTO> storeSummaries = stores.stream()
                .map(storeConverter::toSummaryDTO)
                .toList();
        return storeSummaries;
    }
}
