package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuCategoryDTO;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.MenuCategoryRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

	private final StoreRepository storeRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<MenuCategoryDTO> getMenuCategories(Long storeOwnerId, Long storeId) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		return menuCategoryRepository.findAllByStore_StoreId(storeId)
				.stream()
				.map(category -> MenuCategoryDTO.builder()
						.menuCategoryId(category.getMenuCategoryId())
						.storeId(storeId)
						.menuCategoryName(category.getMenuCategoryName())
						.build())
				.toList();
	}

	@Override
	public MenuCategoryDTO createMenuCategory(Long storeOwnerId, Long storeId, MenuCategoryDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		MenuCategory category = MenuCategory.builder()
				.store(store)
				.menuCategoryName(dto.getMenuCategoryName())
				.build();

		MenuCategory saved = menuCategoryRepository.save(category);

		return MenuCategoryDTO.builder()
				.menuCategoryId(saved.getMenuCategoryId())
				.storeId(storeId)
				.menuCategoryName(saved.getMenuCategoryName())
				.build();
	}

	@Override
	public MenuCategoryDTO updateMenuCategory(Long storeOwnerId, Long storeId, Long menuCategoryId, MenuCategoryDTO dto) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		MenuCategory category = menuCategoryRepository
				.findByMenuCategoryIdAndStore_StoreId(menuCategoryId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

		category.changeName(dto.getMenuCategoryName());

		return MenuCategoryDTO.builder()
				.menuCategoryId(category.getMenuCategoryId())
				.storeId(storeId)
				.menuCategoryName(category.getMenuCategoryName())
				.build();
	}

	@Override
	public void deleteMenuCategory(Long storeOwnerId, Long storeId, Long menuCategoryId) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		MenuCategory category = menuCategoryRepository
				.findByMenuCategoryIdAndStore_StoreId(menuCategoryId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

		menuCategoryRepository.delete(category);
	}
}
