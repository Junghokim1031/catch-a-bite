package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 사용자 + 가게 기준 장바구니 단건 조회
     */
    Optional<Cart> findByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);

    /**
     * 사용자 장바구니 목록 조회
     */
    List<Cart> findAllByAppUser_AppUserId(Long appUserId);

    /**
     * 사용자 + 가게 기준 장바구니 존재 여부 체크
     */
    boolean existsByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);

    /**
     * 사용자 + 가게 기준 장바구니 삭제
     */
    void deleteByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);

    Optional<Cart> findFirstByAppUser_AppUserIdOrderByCartIdDesc(Long appUserId);
}


