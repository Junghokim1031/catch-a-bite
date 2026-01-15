package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuCategoryDTO;

import java.util.List;

public interface MenuCategoryService {

	List<MenuCategoryDTO> getMenuCategories(Long storeOwnerId, Long storeId);

	MenuCategoryDTO createMenuCategory(Long storeOwnerId, Long storeId, MenuCategoryDTO dto);

	MenuCategoryDTO updateMenuCategory(Long storeOwnerId, Long storeId, Long menuCategoryId, MenuCategoryDTO dto);

	void deleteMenuCategory(Long storeOwnerId, Long storeId, Long menuCategoryId);
}
