package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final StoreOwnerRepository storeOwnerRepository;


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

		StoreOwner owner = storeOwnerRepository.findById(storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("사업자 정보를 찾을 수 없습니다. storeOwnerId=" + storeOwnerId));

		Store store = Store.builder()
				.storeOwner(owner) // 추가: FK 세팅(권한체크/조회 쿼리 정상 동작)
				.storeOwnerName(owner.getStoreOwnerName()) // 추가: store_owner_name not null 대응
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


		// PUT은 "전체 수정"으로 취급합니다 (유효성 검증상 필수값을 모두 받는 계약)
		store.changeStoreInfo(
				dto.getStoreName(),
				dto.getStorePhone(),
				dto.getStoreAddress(),
				dto.getStoreCategory(),
				dto.getStoreIntro()
		);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeAddress(store.getStoreAddress())
				.storeCategory(store.getStoreCategory())
				.storeIntro(store.getStoreIntro())
				.build();
	}

	@Override
	public StoreDTO patchStoreBasicInfo(Long storeOwnerId, Long storeId, StorePatchRequestDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		String nextName = dto.getStoreName() != null ? dto.getStoreName() : store.getStoreName();
		String nextPhone = dto.getStorePhone() != null ? dto.getStorePhone() : store.getStorePhone();
		String nextAddress = dto.getStoreAddress() != null ? dto.getStoreAddress() : store.getStoreAddress();
		String nextCategory = dto.getStoreCategory() != null ? dto.getStoreCategory() : store.getStoreCategory();
		String nextIntro = dto.getStoreIntro() != null ? dto.getStoreIntro() : store.getStoreIntro();

		store.changeStoreInfo(nextName, nextPhone, nextAddress, nextCategory, nextIntro);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeAddress(store.getStoreAddress())
				.storeCategory(store.getStoreCategory())
				.storeIntro(store.getStoreIntro())
				.build();
	}

	@Override
	public void changeStoreStatus(Long storeOwnerId, Long storeId, StoreOpenStatus status) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		store.changeStatus(status);
	}

	@Override
	public void changeStoreStatus(Long storeOwnerId, Long storeId, StoreStatusChangeRequestDTO req) {
		changeStoreStatus(storeOwnerId, storeId, req.getStoreOpenStatus());
	}
}
