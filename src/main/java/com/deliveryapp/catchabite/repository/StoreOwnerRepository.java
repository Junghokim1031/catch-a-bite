package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {

    Optional<StoreOwner> findByStoreOwnerEmail(String storeOwnerEmail);

    boolean existsByStoreOwnerEmail(String storeOwnerEmail);

    boolean existsByStoreOwnerMobile(String storeOwnerMobile);
}
