package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, Long> {

    List<MenuOptionGroup> findAllByMenu_MenuId(Long menuId);

    Optional<MenuOptionGroup> findByMenuOptionGroupIdAndMenu_MenuId(Long menuOptionGroupId, Long menuId);

    boolean existsByMenuOptionGroupIdAndMenu_MenuId(Long menuOptionGroupId, Long menuId);

    void deleteAllByMenu_MenuId(Long menuId);
}
