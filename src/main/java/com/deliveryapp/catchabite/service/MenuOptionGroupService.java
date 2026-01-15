package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuOptionGroupDTO;

public interface MenuOptionGroupService {

	void createOptionGroup(Long storeOwnerId, Long menuId, MenuOptionGroupDTO dto);

	void updateOptionGroup(Long storeOwnerId, Long menuId, Long menuOptionGroupId, MenuOptionGroupDTO dto);

	void deleteOptionGroup(Long storeOwnerId, Long menuId, Long menuOptionGroupId);
}
