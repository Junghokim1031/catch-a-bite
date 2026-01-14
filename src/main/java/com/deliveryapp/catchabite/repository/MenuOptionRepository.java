package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    List<MenuOption> findAllByMenuOptionGroup_MenuOptionGroupId(Long menuOptionGroupId);

    Optional<MenuOption> findByMenuOptionIdAndMenuOptionGroup_MenuOptionGroupId(Long menuOptionId, Long menuOptionGroupId);

    boolean existsByMenuOptionIdAndMenuOptionGroup_MenuOptionGroupId(Long menuOptionId, Long menuOptionGroupId);

    void deleteAllByMenuOptionGroup_MenuOptionGroupId(Long menuOptionGroupId);
}
