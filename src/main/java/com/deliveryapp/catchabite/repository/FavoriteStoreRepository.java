package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {
    // 특정 사용자의 즐겨찾기 목록 조회
    List<FavoriteStore> findByAppUser_AppUserId(Long appUserId);
    
    // 이미 즐겨찾기 등록되었는지 확인
    boolean existsByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);
}