package com.deliveryapp.catchabite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliveryapp.catchabite.entity.DelivererSettlement;

public interface DelivererSettlementRepository extends JpaRepository<DelivererSettlement, Long> {
    List<DelivererSettlement> findByDeliverer_DelivererIdOrderByRequestedAtDesc(Long delivererId);
    Optional<DelivererSettlement> findBySettlementIdAndDeliverer_DelivererId(Long settlementId, Long delivererId);
}
