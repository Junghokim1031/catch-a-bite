package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.MenuOptionDTO;
import java.util.List;

public interface MenuOptionService {

    void createOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, MenuOptionDTO dto);

    void updateOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, Long menuOptionId, MenuOptionDTO dto);

    void deleteOption(Long storeOwnerId, Long menuId, Long menuOptionGroupId, Long menuOptionId);

    List<MenuOptionDTO> listOptions(Long storeOwnerId, Long menuId, Long menuOptionGroupId);
}
