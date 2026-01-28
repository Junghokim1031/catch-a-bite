package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.StoreCategory;
import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.dto.OwnerBusinessInfoDTO;
import com.deliveryapp.catchabite.dto.OwnerBusinessInfoPatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreDeliveryConditionPatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreOriginLabelDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
				.storeCategory(store.getStoreCategory().name())
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
				.storeOwner(owner)
				.storeOwnerName(owner.getStoreOwnerName())
				.storeName(dto.getStoreName())
				.storePhone(dto.getStorePhone())
				.storeAddress(dto.getStoreAddress())
				.storeCategory(StoreCategory.from(dto.getStoreCategory()))
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
				.storeCategory(saved.getStoreCategory().name())
				.storeMinOrder(saved.getStoreMinOrder())
				.storeMaxDist(saved.getStoreMaxDist())
				.storeDeliveryFee(saved.getStoreDeliveryFee())
				.storeOpenTime(saved.getStoreOpenTime())
				.storeCloseTime(saved.getStoreCloseTime())
				.storeOpenStatus(saved.getStoreOpenStatus())
				.storeIntro(saved.getStoreIntro())
				.build();
	}

	@Override
	public StoreDTO updateStoreBasicInfo(Long storeOwnerId, Long storeId, StoreDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		// PUT은 "전체 수정"
		store.changeStoreInfo(
				dto.getStoreName(),
				dto.getStorePhone(),
				dto.getStoreAddress(),
				StoreCategory.from(dto.getStoreCategory()),
				dto.getStoreIntro()
		);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeAddress(store.getStoreAddress())
				.storeCategory(store.getStoreCategory().name())
				.storeOpenStatus(store.getStoreOpenStatus())
				.storeIntro(store.getStoreIntro())
				.storeMinOrder(store.getStoreMinOrder())
				.storeMaxDist(store.getStoreMaxDist())
				.storeDeliveryFee(store.getStoreDeliveryFee())
				.storeOpenTime(store.getStoreOpenTime())
				.storeCloseTime(store.getStoreCloseTime())
				.build();
	}

	@Override
	public StoreDTO patchStoreBasicInfo(Long storeOwnerId, Long storeId, StorePatchRequestDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		String nextName = dto.getStoreName() != null ? dto.getStoreName() : store.getStoreName();
		String nextPhone = dto.getStorePhone() != null ? dto.getStorePhone() : store.getStorePhone();
		String nextAddress = dto.getStoreAddress() != null ? dto.getStoreAddress() : store.getStoreAddress();

		StoreCategory nextCategory = store.getStoreCategory();
		if (dto.getStoreCategory() != null) {
			nextCategory = StoreCategory.from(dto.getStoreCategory());
		}

		String nextIntro = dto.getStoreIntro() != null ? dto.getStoreIntro() : store.getStoreIntro();

		Integer nextMinOrder = dto.getStoreMinOrder() != null ? dto.getStoreMinOrder() : store.getStoreMinOrder();
		Integer nextMaxDist = dto.getStoreMaxDist() != null ? dto.getStoreMaxDist() : store.getStoreMaxDist();
		Integer nextDeliveryFee = dto.getStoreDeliveryFee() != null ? dto.getStoreDeliveryFee() : store.getStoreDeliveryFee();

		store.changeStoreInfo(nextName, nextPhone, nextAddress, nextCategory, nextIntro);
		store.changeDeliveryCondition(nextMinOrder, nextMaxDist, nextDeliveryFee);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.storePhone(store.getStorePhone())
				.storeAddress(store.getStoreAddress())
				.storeCategory(store.getStoreCategory().name())
				.storeOpenStatus(store.getStoreOpenStatus())
				.storeIntro(store.getStoreIntro())
				.storeMinOrder(store.getStoreMinOrder())
				.storeMaxDist(store.getStoreMaxDist())
				.storeDeliveryFee(store.getStoreDeliveryFee())
				.storeOpenTime(store.getStoreOpenTime())
				.storeCloseTime(store.getStoreCloseTime())
				.build();
	}

	@Override
	public StoreDTO patchStoreDeliveryCondition(Long storeOwnerId, Long storeId, StoreDeliveryConditionPatchRequestDTO dto) {

		if (dto.getStoreMinOrder() == null && dto.getStoreMaxDist() == null && dto.getStoreDeliveryFee() == null) {
			throw new IllegalArgumentException("변경할 배달조건 값이 없습니다.");
		}

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		Integer nextMinOrder = dto.getStoreMinOrder() != null ? dto.getStoreMinOrder() : store.getStoreMinOrder();
		Integer nextMaxDist = dto.getStoreMaxDist() != null ? dto.getStoreMaxDist() : store.getStoreMaxDist();
		Integer nextDeliveryFee = dto.getStoreDeliveryFee() != null ? dto.getStoreDeliveryFee() : store.getStoreDeliveryFee();

		store.changeDeliveryCondition(nextMinOrder, nextMaxDist, nextDeliveryFee);

		return StoreDTO.builder()
				.storeId(store.getStoreId())
				.storeMinOrder(store.getStoreMinOrder())
				.storeMaxDist(store.getStoreMaxDist())
				.storeDeliveryFee(store.getStoreDeliveryFee())
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

	@Override
	@Transactional(readOnly = true)
	public List<StoreSummaryDTO> getMyStores(Long storeOwnerId) {

		List<Store> stores = storeRepository.findAllByStoreOwner_StoreOwnerId(storeOwnerId);

		return stores.stream()
				.map(s -> StoreSummaryDTO.builder()
						.storeId(s.getStoreId())
						.storeName(s.getStoreName())
						.storeCategory(s.getStoreCategory().name())
						.storeAddress(s.getStoreAddress())
						.storeOpenStatus(s.getStoreOpenStatus())
						.build())
				.toList();
	}

	// ====== 피그마: 사업자 정보 ======
	@Override
	@Transactional(readOnly = true)
	public OwnerBusinessInfoDTO getBusinessInfo(Long storeOwnerId, Long storeId) {
		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		StoreOwner owner = store.getStoreOwner();
		return OwnerBusinessInfoDTO.builder()
				.ownerName(owner != null ? owner.getStoreOwnerName() : store.getStoreOwnerName())
				.businessName(store.getStoreName())
				.businessAddress(store.getStoreAddress())
				.businessRegistrationNo(owner != null ? owner.getStoreOwnerBusinessRegistrationNo() : null)
				.build();
	}

	@Override
	public OwnerBusinessInfoDTO patchBusinessInfo(Long storeOwnerId, Long storeId, OwnerBusinessInfoPatchRequestDTO dto) {
		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		StoreOwner owner = store.getStoreOwner();
		if (owner == null) {
			throw new IllegalStateException("매장에 사업자 계정이 연결되어 있지 않습니다. storeId=" + storeId);
		}

		// 대표자명
		if (dto.getOwnerName() != null) {
			owner.changeName(dto.getOwnerName());
			store.changeOwnerSnapshotName(dto.getOwnerName());
		}

		// 상호명/사업자주소는 store에 반영
		if (dto.getBusinessName() != null || dto.getBusinessAddress() != null) {
			String nextName = dto.getBusinessName() != null ? dto.getBusinessName() : store.getStoreName();
			String nextAddress = dto.getBusinessAddress() != null ? dto.getBusinessAddress() : store.getStoreAddress();

			store.changeStoreInfo(
					nextName,
					store.getStorePhone(),
					nextAddress,
					store.getStoreCategory(),
					store.getStoreIntro()
			);
		}

		// 사업자등록번호
		if (dto.getBusinessRegistrationNo() != null) {
			owner.changeBusinessRegistrationNo(dto.getBusinessRegistrationNo());
		}

		return getBusinessInfo(storeOwnerId, storeId);
	}

	// ====== 피그마: 원산지 표기(텍스트) ======
	@Override
	@Transactional(readOnly = true)
	public StoreOriginLabelDTO getOriginLabel(Long storeOwnerId, Long storeId) {
		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		return StoreOriginLabelDTO.builder()
				.originLabel(store.getStoreOriginLabel())
				.build();
	}

	@Override
	public StoreOriginLabelDTO patchOriginLabel(Long storeOwnerId, Long storeId, StoreOriginLabelDTO dto) {
		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		store.changeOriginLabel(dto.getOriginLabel());

		return StoreOriginLabelDTO.builder()
				.originLabel(store.getStoreOriginLabel())
				.build();
	}
}
