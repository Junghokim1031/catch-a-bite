package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 매장 전체 메뉴 조회
    List<Menu> findAllByStore_StoreId(Long storeId);

    // 카테고리별 메뉴 조회
    List<Menu> findAllByStore_StoreIdAndMenuCategory_MenuCategoryId(Long storeId, Long menuCategoryId);

    // 매장 스코프에서 메뉴 단건 조회(권한 체크용)
    Optional<Menu> findByMenuIdAndStore_StoreId(Long menuId, Long storeId);

    // 매장 스코프에서 메뉴 존재 여부(권한 체크용)
    boolean existsByMenuIdAndStore_StoreId(Long menuId, Long storeId);

    // 매장 삭제/정리 시 메뉴 전체 삭제가 필요하면 사용(정책에 따라)
    void deleteAllByStore_StoreId(Long storeId);
}
