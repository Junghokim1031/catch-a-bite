package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.StoreOwner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 가게 사장(StoreOwner) 조회 및 중복 검사용 JPA 레포지토리
 */
public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {

    // 이메일로 사장 계정 조회
    Optional<StoreOwner> findByStoreOwnerEmail(String storeOwnerEmail);

    // 이메일 중복 여부 확인
    boolean existsByStoreOwnerEmail(String storeOwnerEmail);

    // 휴대폰 번호 중복 여부 확인
    boolean existsByStoreOwnerMobile(String storeOwnerMobile);
}
