package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByStore_StoreId(Long storeId, Pageable pageable);

    Optional<Review> findByReviewIdAndStore_StoreId(Long reviewId, Long storeId);
  
    Optional<Review> findByStoreOrderOrderId(Long storeOrderId);

    long countByStore_StoreId(Long storeId);

    @Query("SELECT AVG(r.reviewRating) FROM Review r WHERE r.store.storeId = :storeId")
    Double getAverageRating(@Param("storeId") Long storeId);
}
