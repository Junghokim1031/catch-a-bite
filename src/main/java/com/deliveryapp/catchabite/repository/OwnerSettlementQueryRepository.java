package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;
import com.deliveryapp.catchabite.dto.OwnerSettlementSummaryDTO;
import com.deliveryapp.catchabite.entity.OwnerSettlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OwnerSettlementQueryRepository extends JpaRepository<OwnerSettlement, Long> {

	@Query(
			value = """
				select new com.deliveryapp.catchabite.dto.OwnerSettlementSummaryDTO(
					s.ownerSettlementId,
					s.periodStart,
					s.periodEnd,
					s.totalGrossAmount,
					s.totalFeeAmount,
					s.totalNetAmount,
					s.ownerSettlementStatus,
					s.createdAt,
					s.paidAt
				)
				from OwnerSettlement s
				where s.storeOwner.storeOwnerId = :storeOwnerId
				  and (:status is null or s.ownerSettlementStatus = :status)
				  and (:fromDate is null or s.periodStart >= :fromDate)
				  and (:toDate is null or s.periodEnd <= :toDate)
			""",
			countQuery = """
				select count(s)
				from OwnerSettlement s
				where s.storeOwner.storeOwnerId = :storeOwnerId
				  and (:status is null or s.ownerSettlementStatus = :status)
				  and (:fromDate is null or s.periodStart >= :fromDate)
				  and (:toDate is null or s.periodEnd <= :toDate)
			"""
	)
	Page<OwnerSettlementSummaryDTO> findOwnerSettlements(
			@Param("storeOwnerId") Long storeOwnerId,
			@Param("status") OwnerSettlementStatus status,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			Pageable pageable
	);
}
