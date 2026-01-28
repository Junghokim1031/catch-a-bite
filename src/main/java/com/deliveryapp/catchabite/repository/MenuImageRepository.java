package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {

    List<MenuImage> findAllByMenu_MenuIdOrderByMenuImageIsMainDescMenuImageIdAsc(Long menuId);

    Optional<MenuImage> findByMenuImageIdAndMenu_MenuId(Long menuImageId, Long menuId);

    List<MenuImage> findAllByMenu_MenuId(Long menuId);

    // Find image by single menu ID
    Optional<MenuImage> findByMenu_MenuId(Long menuId);    

    // Efficiently fetch images for a list of menus (to avoid N+1 problem)
    List<MenuImage> findByMenu_MenuIdIn(List<Long> menuIds);
}
