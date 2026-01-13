package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

    List<StoreImage> findAllByStore_StoreId(Long storeId);

    Optional<StoreImage> findByStoreImageIdAndStore_StoreId(Long storeImageId, Long storeId);

    void deleteAllByStore_StoreId(Long storeId);
}
