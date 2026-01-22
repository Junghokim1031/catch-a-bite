package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuCategoryWithMenusDTO;
import com.deliveryapp.catchabite.dto.UserMenuDetailDTO;

import java.util.List;

public interface UserMenuCategoryService {

	UserMenuDetailDTO getMenuDetail(Long menuId);
	
	List<MenuCategoryWithMenusDTO> getMenuBoardForUser(Long storeId);
}
