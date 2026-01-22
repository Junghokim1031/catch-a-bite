package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;

import java.util.List;

public interface UserMenuCategoryService {

	List<MenuCategoryWithMenusDTO> getMenuBoardForUser(Long storeId);
}
