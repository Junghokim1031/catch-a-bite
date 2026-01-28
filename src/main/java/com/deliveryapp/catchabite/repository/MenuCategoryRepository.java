package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findAllByStore_StoreId(Long storeId);

    Optional<MenuCategory> findByMenuCategoryIdAndStore_StoreId(Long menuCategoryId, Long storeId);

    boolean existsByMenuCategoryIdAndStore_StoreId(Long menuCategoryId, Long storeId);

    void deleteAllByStore_StoreId(Long storeId);

    /**
     * 카테고리와 해당 카테고리의 메뉴들을 한 번에 조회 (Fetch Join)
     * N+1 문제 해결: 카테고리 개수만큼 추가 쿼리가 나가는 것을 방지
     */
    @Query("SELECT DISTINCT mc FROM MenuCategory mc " +
           "LEFT JOIN FETCH mc.menus m " +
           "WHERE mc.store.storeId = :storeId " +
           "ORDER BY mc.menuCategoryId ASC")
    List<MenuCategory> findAllWithMenusByStoreId(@Param("storeId") Long storeId);
}
