package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuOptionGroupDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuOptionGroup;
import com.deliveryapp.catchabite.repository.MenuOptionGroupRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuOptionGroupServiceImpl implements MenuOptionGroupService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuOptionGroupRepository menuOptionGroupRepository;

	@Override
	public void createOptionGroup(Long storeOwnerId, Long menuId, MenuOptionGroupDTO dto) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		MenuOptionGroup group = MenuOptionGroup.builder()
				.menu(menu)
				.menuOptionGroupName(dto.getMenuOptionGroupName())
				.menuOptionGroupRequired(dto.getRequired())
				.build();

		menuOptionGroupRepository.save(group);
	}

	@Override
	public void updateOptionGroup(Long storeOwnerId, Long menuId, Long menuOptionGroupId, MenuOptionGroupDTO dto) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		MenuOptionGroup group = menuOptionGroupRepository
				.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		group.changeInfo(
				dto.getMenuOptionGroupName(),
				dto.getRequired()
		);
	}

	@Override
	public void deleteOptionGroup(Long storeOwnerId, Long menuId, Long menuOptionGroupId) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		MenuOptionGroup group = menuOptionGroupRepository
				.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		menuOptionGroupRepository.delete(group);
	}
}
