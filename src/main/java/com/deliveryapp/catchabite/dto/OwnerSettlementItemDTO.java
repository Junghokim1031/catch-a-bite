package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;

import java.time.LocalDateTime;

public record OwnerSettlementItemDTO(
		Long ownerSettlementItemId,
		Long ownerSettlementId,
		Long orderId,
		Long storeId,
		String storeName,
		Long paymentId,
		LocalDateTime paymentPaidAt,
		Long grossAmount,
		Long platformFeeAmount,
		Long pgFeeAmount,
		Long netAmount,
		OwnerSettlementItemStatus ownerSettlementItemStatus
) {
}
