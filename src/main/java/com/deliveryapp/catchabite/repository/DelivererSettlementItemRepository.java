package com.deliveryapp.catchabite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliveryapp.catchabite.entity.DelivererSettlementItem;

public interface DelivererSettlementItemRepository extends JpaRepository<DelivererSettlementItem, Long> {
    List<DelivererSettlementItem> findBySettlement_SettlementId(Long settlementId);
    boolean existsByOrderDelivery_DeliveryId(Long deliveryId);
}
