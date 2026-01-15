package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuOptionDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuOption;
import com.deliveryapp.catchabite.entity.MenuOptionGroup;
import com.deliveryapp.catchabite.repository.MenuOptionGroupRepository;
import com.deliveryapp.catchabite.repository.MenuOptionRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuOptionServiceImpl implements MenuOptionService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuOptionGroupRepository menuOptionGroupRepository;
	private final MenuOptionRepository menuOptionRepository;

	@Override
	public void createOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, MenuOptionDTO dto) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		MenuOptionGroup group = menuOptionGroupRepository
				.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		MenuOption option = MenuOption.builder()
				.menuOptionGroup(group)
				.menuOptionName(dto.getMenuOptionName())
				.menuOptionPrice(dto.getMenuOptionPrice())
				.build();

		menuOptionRepository.save(option);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuOptionDTO> listOptions(Long storeOwnerId, Long menuId, Long menuOptionGroupId) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		// 그룹이 해당 메뉴 소속인지 검증
		menuOptionGroupRepository.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		return menuOptionRepository.findAllByMenuOptionGroup_MenuOptionGroupId(menuOptionGroupId)
				.stream()
				.map(option -> MenuOptionDTO.builder()
						.menuOptionId(option.getMenuOptionId())
						.menuOptionGroupId(option.getMenuOptionGroup().getMenuOptionGroupId())
						.menuOptionName(option.getMenuOptionName())
						.menuOptionPrice(option.getMenuOptionPrice())
						.build())
				.toList();
	}

	@Override
	public void updateOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, Long menuOptionId, MenuOptionDTO dto) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		// 그룹이 해당 메뉴 소속인지 검증
		menuOptionGroupRepository.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		MenuOption option = menuOptionRepository
				.findByMenuOptionIdAndMenuOptionGroup_MenuOptionGroupId(menuOptionId, menuOptionGroupId)
				.orElseThrow(() -> new IllegalArgumentException("option not found"));

		option.changeInfo(dto.getMenuOptionName(), dto.getMenuOptionPrice());
	}

	@Override
	public void deleteOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, Long menuOptionId) {

		Menu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalArgumentException("menu not found"));

		Long storeId = menu.getStore().getStoreId();

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("not your store"));

		// 그룹이 해당 메뉴 소속인지 검증
		menuOptionGroupRepository.findByMenuOptionGroupIdAndMenu_MenuId(menuOptionGroupId, menuId)
				.orElseThrow(() -> new IllegalArgumentException("option group not found"));

		MenuOption option = menuOptionRepository
				.findByMenuOptionIdAndMenuOptionGroup_MenuOptionGroupId(menuOptionId, menuOptionGroupId)
				.orElseThrow(() -> new IllegalArgumentException("option not found"));

		menuOptionRepository.delete(option);
	}
}
