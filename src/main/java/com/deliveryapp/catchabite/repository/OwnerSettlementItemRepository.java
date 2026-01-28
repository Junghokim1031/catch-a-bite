package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.entity.OwnerSettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OwnerSettlementItemRepository extends JpaRepository<OwnerSettlementItem, Long> {

	boolean existsByStoreOrder_OrderId(Long orderId);

	Optional<OwnerSettlementItem> findByStoreOrder_OrderId(Long orderId);

	List<OwnerSettlementItem> findByStoreOwner_StoreOwnerIdAndOwnerSettlementIsNullAndOwnerSettlementItemStatusAndPaymentPaidAtBetween(
			Long storeOwnerId,
			OwnerSettlementItemStatus ownerSettlementItemStatus,
			LocalDateTime fromAt,
			LocalDateTime toAt
	);

	List<OwnerSettlementItem> findByOwnerSettlement_OwnerSettlementId(Long ownerSettlementId);
}
