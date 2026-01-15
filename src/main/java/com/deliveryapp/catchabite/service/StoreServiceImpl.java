package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;

	@Override
	@Transactional(readOnly = true)
	public StoreDTO getStoreInfo(Long storeOwnerId, Long storeId) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeAddress(store.getStoreAddress())
				.storeCategory(store.getStoreCategory())
				.storeMinOrder(store.getStoreMinOrder())
				.storeMaxDist(store.getStoreMaxDist())
				.storeDeliveryFee(store.getStoreDeliveryFee())
				.storeOpenTime(store.getStoreOpenTime())
				.storeCloseTime(store.getStoreCloseTime())
				.storeOpenStatus(store.getStoreOpenStatus())
				.storeIntro(store.getStoreIntro())
				.build();
	}

	@Override
	public StoreDTO createStore(Long storeOwnerId, StoreDTO dto) {

		// NOTE:
		// StoreOwner 연관은 로그인 이후 컨텍스트로 처리
		// (ERD상 FK가 nullable 허용된 현재 설계 기준)

		Store store = Store.builder()
				.storeName(dto.getStoreName())
				.storePhone(dto.getStorePhone())
				.storeAddress(dto.getStoreAddress())
				.storeCategory(dto.getStoreCategory())
				.storeMinOrder(dto.getStoreMinOrder())
				.storeMaxDist(dto.getStoreMaxDist())
				.storeDeliveryFee(dto.getStoreDeliveryFee())
				.storeOpenTime(dto.getStoreOpenTime())
				.storeCloseTime(dto.getStoreCloseTime())
				.storeIntro(dto.getStoreIntro())
				.storeOpenStatus(StoreOpenStatus.CLOSE)
				.build();

		Store saved = storeRepository.save(store);

		return StoreDTO.builder()
				.storeId(saved.getStoreId())
				.storeName(saved.getStoreName())
				.storePhone(saved.getStorePhone())
				.storeAddress(saved.getStoreAddress())
				.storeCategory(saved.getStoreCategory())
				.storeMinOrder(saved.getStoreMinOrder())    
				.storeMaxDist(saved.getStoreMaxDist())     
				.storeDeliveryFee(saved.getStoreDeliveryFee()) 
				.build();
	}

	@Override
	public StoreDTO updateStoreBasicInfo(Long storeOwnerId, Long storeId, StoreDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		store.changeBasicInfo(
				dto.getStoreName(),
				dto.getStorePhone(),
				dto.getStoreIntro()
		);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeIntro(store.getStoreIntro())
				.build();
	}

	@Override
	public void changeStoreStatus(Long storeOwnerId, Long storeId, StoreOpenStatus status) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		store.changeStatus(status);
	}

}
