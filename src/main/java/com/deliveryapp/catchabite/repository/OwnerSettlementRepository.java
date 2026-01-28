package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.OwnerSettlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerSettlementRepository extends JpaRepository<OwnerSettlement, Long> {

	Optional<OwnerSettlement> findByOwnerSettlementIdAndStoreOwner_StoreOwnerId(Long ownerSettlementId, Long storeOwnerId);
}
