package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;
import com.deliveryapp.catchabite.dto.OwnerSettlementItemDTO;
import com.deliveryapp.catchabite.dto.OwnerSettlementSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OwnerSettlementService {

	Page<OwnerSettlementItemDTO> listSettlementItems(
			Long storeOwnerId,
			Long storeId,
			Long ownerSettlementId,
			OwnerSettlementItemStatus status,
			LocalDate from,
			LocalDate to,
			Pageable pageable
	);

	Page<OwnerSettlementSummaryDTO> listSettlements(
			Long storeOwnerId,
			OwnerSettlementStatus status,
			LocalDate from,
			LocalDate to,
			Pageable pageable
	);

	/**
	 * 기간별 정산 헤더를 생성하고, 기간 내 PENDING 라인들을 포함 처리합니다.
	 */
	Long createSettlement(Long storeOwnerId, LocalDate periodStart, LocalDate periodEnd);

	/**
	 * 기간별 정산을 '지급 완료' 처리하고 STORE_PAYOUT 트랜잭션을 기록합니다.
	 */
	void payoutSettlement(Long storeOwnerId, Long ownerSettlementId, String portoneTransferId);
}
