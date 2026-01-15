package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.StoreImageDTO;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreImage;
import com.deliveryapp.catchabite.repository.StoreImageRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreImageServiceImpl implements StoreImageService {

	private final StoreRepository storeRepository;
	private final StoreImageRepository storeImageRepository;

	@Override
	@Transactional(readOnly = true)
	public List<StoreImageDTO> getStoreImages(Long storeOwnerId, Long storeId) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		return storeImageRepository.findAllByStore_StoreId(storeId)
				.stream()
				.map(image -> StoreImageDTO.builder()
						.storeImageId(image.getStoreImageId())
						.storeId(store.getStoreId())
						.storeImageUrl(image.getStoreImageUrl())
						.build())
				.toList();
	}

	@Override
	public StoreImageDTO createStoreImage(Long storeOwnerId, Long storeId, StoreImageDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		StoreImage image = StoreImage.builder()
				.store(store)
				.storeImageUrl(dto.getStoreImageUrl())
				.build();

		StoreImage saved = storeImageRepository.save(image);

		return StoreImageDTO.builder()
				.storeImageId(saved.getStoreImageId())
				.storeId(storeId)
				.storeImageUrl(saved.getStoreImageUrl())
				.build();
	}

	@Override
	public void deleteStoreImage(Long storeOwnerId, Long storeId, Long storeImageId) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		StoreImage image = storeImageRepository
				.findByStoreImageIdAndStore_StoreId(storeImageId, store.getStoreId())
				.orElseThrow(() -> new IllegalArgumentException("이미지가 존재하지 않습니다."));

		storeImageRepository.delete(image);
	}
}
