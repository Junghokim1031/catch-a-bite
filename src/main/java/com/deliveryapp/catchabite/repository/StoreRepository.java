package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 사업자(오너) 소유 매장 단건 조회 (권한 체크 포함)
     */
    Optional<Store> findByStoreIdAndStoreOwner_StoreOwnerId(Long storeId, Long storeOwnerId);

    /**
     * 사업자(오너) 소유 매장 목록 조회
     */
    List<Store> findAllByStoreOwner_StoreOwnerId(Long storeOwnerId);

    /**
     * 사업자(오너) 소유 여부만 빠르게 체크
     */
    boolean existsByStoreIdAndStoreOwner_StoreOwnerId(Long storeId, Long storeOwnerId);
}
