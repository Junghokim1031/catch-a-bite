package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.dto.OwnerSettlementItemDTO;
import com.deliveryapp.catchabite.entity.OwnerSettlementItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OwnerSettlementItemQueryRepository extends JpaRepository<OwnerSettlementItem, Long> {

	@Query(
			value = """
				select new com.deliveryapp.catchabite.dto.OwnerSettlementItemDTO(
					i.ownerSettlementItemId,
					case when i.ownerSettlement is null then null else i.ownerSettlement.ownerSettlementId end,
					i.storeOrder.orderId,
					i.store.storeId,
					i.store.storeName,
					i.payment.paymentId,
					i.paymentPaidAt,
					i.grossAmount,
					i.platformFeeAmount,
					i.pgFeeAmount,
					i.netAmount,
					i.ownerSettlementItemStatus
				)
				from OwnerSettlementItem i
				where i.storeOwner.storeOwnerId = :storeOwnerId
				  and (:storeId is null or i.store.storeId = :storeId)
				  and (:ownerSettlementId is null or i.ownerSettlement.ownerSettlementId = :ownerSettlementId)
				  and (:status is null or i.ownerSettlementItemStatus = :status)
				  and (:fromAt is null or i.paymentPaidAt >= :fromAt)
				  and (:toAt is null or i.paymentPaidAt < :toAt)
			""",
			countQuery = """
				select count(i)
				from OwnerSettlementItem i
				where i.storeOwner.storeOwnerId = :storeOwnerId
				  and (:storeId is null or i.store.storeId = :storeId)
				  and (:ownerSettlementId is null or i.ownerSettlement.ownerSettlementId = :ownerSettlementId)
				  and (:status is null or i.ownerSettlementItemStatus = :status)
				  and (:fromAt is null or i.paymentPaidAt >= :fromAt)
				  and (:toAt is null or i.paymentPaidAt < :toAt)
			"""
	)
	Page<OwnerSettlementItemDTO> findOwnerSettlementItems(
			@Param("storeOwnerId") Long storeOwnerId,
			@Param("storeId") Long storeId,
			@Param("ownerSettlementId") Long ownerSettlementId,
			@Param("status") OwnerSettlementItemStatus status,
			@Param("fromAt") LocalDateTime fromAt,
			@Param("toAt") LocalDateTime toAt,
			Pageable pageable
	);
}
