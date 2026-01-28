package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OwnerSettlementSummaryDTO(
		Long ownerSettlementId,
		LocalDate periodStart,
		LocalDate periodEnd,
		Long totalGrossAmount,
		Long totalFeeAmount,
		Long totalNetAmount,
		OwnerSettlementStatus ownerSettlementStatus,
		LocalDateTime createdAt,
		LocalDateTime paidAt
) {
}
