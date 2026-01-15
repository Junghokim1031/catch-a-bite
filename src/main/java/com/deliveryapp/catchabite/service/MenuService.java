package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuDTO;

import java.util.List;

public interface MenuService {

	List<MenuDTO> getMenus(Long storeOwnerId, Long storeId, Long menuCategoryId);

	MenuDTO createMenu(Long storeOwnerId, Long storeId, MenuDTO dto);

	MenuDTO updateMenu(Long storeOwnerId, Long storeId, Long menuId, MenuDTO dto);

	void changeMenuAvailability(Long storeOwnerId, Long storeId, Long menuId, Boolean isAvailable);

	void deleteMenu(Long storeOwnerId, Long storeId, Long menuId);
}
