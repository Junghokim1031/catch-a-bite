package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.entity.Menu;
import com.deliveryapp.catchabite.entity.MenuCategory;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.repository.MenuCategoryRepository;
import com.deliveryapp.catchabite.repository.MenuRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<MenuDTO> getMenus(Long storeOwnerId, Long storeId, Long menuCategoryId) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		List<Menu> menus = (menuCategoryId == null)
				? menuRepository.findAllByStore_StoreId(storeId)
				: menuRepository.findAllByStore_StoreIdAndMenuCategory_MenuCategoryId(storeId, menuCategoryId);

		return menus.stream()
				.map(menu -> MenuDTO.builder()
						.menuId(menu.getMenuId())
						.storeId(storeId)
						.menuCategoryId(menu.getMenuCategory().getMenuCategoryId())
						.menuName(menu.getMenuName())
						.menuPrice(menu.getMenuPrice())
						.menuDescription(menu.getMenuDescription())
						.menuIsAvailable(menu.getMenuIsAvailable())
						.build())
				.toList();
	}

	@Override
	public MenuDTO createMenu(Long storeOwnerId, Long storeId, MenuDTO dto) {

		Store store = storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		MenuCategory category = menuCategoryRepository
				.findByMenuCategoryIdAndStore_StoreId(dto.getMenuCategoryId(), storeId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

		Menu menu = Menu.builder()
				.store(store)
				.menuCategory(category)
				.menuName(dto.getMenuName())
				.menuPrice(dto.getMenuPrice())
				.menuDescription(dto.getMenuDescription())
				.menuIsAvailable(dto.getMenuIsAvailable() == null ? true : dto.getMenuIsAvailable())
				.build();

		Menu saved = menuRepository.save(menu);

		return MenuDTO.builder()
				.menuId(saved.getMenuId())
				.storeId(storeId)
				.menuCategoryId(category.getMenuCategoryId())
				.menuName(saved.getMenuName())
				.menuPrice(saved.getMenuPrice())
				.menuDescription(saved.getMenuDescription())
				.menuIsAvailable(saved.getMenuIsAvailable())
				.build();
	}

	@Override
	public MenuDTO updateMenu(Long storeOwnerId, Long storeId, Long menuId, MenuDTO dto) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

		MenuCategory category = menuCategoryRepository
				.findByMenuCategoryIdAndStore_StoreId(dto.getMenuCategoryId(), storeId)
				.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

		menu.changeInfo(category, dto.getMenuName(), dto.getMenuPrice(), dto.getMenuDescription());

		return MenuDTO.builder()
				.menuId(menu.getMenuId())
				.storeId(storeId)
				.menuCategoryId(category.getMenuCategoryId())
				.menuName(menu.getMenuName())
				.menuPrice(menu.getMenuPrice())
				.menuDescription(menu.getMenuDescription())
				.menuIsAvailable(menu.getMenuIsAvailable())
				.build();
	}

	@Override
	public void changeMenuAvailability(Long storeOwnerId, Long storeId, Long menuId, Boolean isAvailable) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

		menu.changeAvailability(isAvailable);
	}

	@Override
	public void deleteMenu(Long storeOwnerId, Long storeId, Long menuId) {

		storeRepository.findByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId));

		Menu menu = menuRepository.findByMenuIdAndStore_StoreId(menuId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다. menuId=" + menuId));

		menuRepository.delete(menu);
	}
}
