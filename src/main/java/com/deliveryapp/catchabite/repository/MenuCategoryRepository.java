package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findAllByStore_StoreId(Long storeId);

    Optional<MenuCategory> findByMenuCategoryIdAndStore_StoreId(Long menuCategoryId, Long storeId);

    boolean existsByMenuCategoryIdAndStore_StoreId(Long menuCategoryId, Long storeId);

    void deleteAllByStore_StoreId(Long storeId);
}
