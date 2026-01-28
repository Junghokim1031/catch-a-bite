package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {
    // Find image by single menu ID
    Optional<MenuImage> findByMenu_MenuId(Long menuId);

    // Efficiently fetch images for a list of menus (to avoid N+1 problem)
    List<MenuImage> findByMenu_MenuIdIn(List<Long> menuIds);
}