package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;
import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import com.deliveryapp.catchabite.dto.OwnerSettlementItemDTO;
import com.deliveryapp.catchabite.dto.OwnerSettlementSummaryDTO;
import com.deliveryapp.catchabite.entity.OwnerSettlement;
import com.deliveryapp.catchabite.entity.OwnerSettlementItem;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.OwnerSettlementItemQueryRepository;
import com.deliveryapp.catchabite.repository.OwnerSettlementItemRepository;
import com.deliveryapp.catchabite.repository.OwnerSettlementQueryRepository;
import com.deliveryapp.catchabite.repository.OwnerSettlementRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import com.deliveryapp.catchabite.transaction.entity.Transaction;
import com.deliveryapp.catchabite.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerSettlementServiceImpl implements OwnerSettlementService {

	private final OwnerSettlementRepository ownerSettlementRepository;
	private final OwnerSettlementQueryRepository ownerSettlementQueryRepository;
	private final OwnerSettlementItemRepository ownerSettlementItemRepository;
	private final OwnerSettlementItemQueryRepository ownerSettlementItemQueryRepository;
	private final StoreRepository storeRepository;
	private final StoreOwnerRepository storeOwnerRepository;
	private final TransactionService transactionService;

	@Override
	public Page<OwnerSettlementItemDTO> listSettlementItems(
			Long storeOwnerId,
			Long storeId,
			Long ownerSettlementId,
			OwnerSettlementItemStatus status,
			LocalDate from,
			LocalDate to,
			Pageable pageable
	) {
		if (storeOwnerId == null) {
			throw new IllegalArgumentException("storeOwnerId is required");
		}

		if (storeId != null) {
			if (!storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)) {
				throw new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId);
			}
		}

		LocalDateTime fromAt = (from == null) ? null : from.atStartOfDay();
		LocalDateTime toAt = (to == null) ? null : to.plusDays(1).atStartOfDay();

		return ownerSettlementItemQueryRepository.findOwnerSettlementItems(
				storeOwnerId,
				storeId,
				ownerSettlementId,
				status,
				fromAt,
				toAt,
				pageable
		);
	}

	@Override
	public Page<OwnerSettlementSummaryDTO> listSettlements(
			Long storeOwnerId,
			OwnerSettlementStatus status,
			LocalDate from,
			LocalDate to,
			Pageable pageable
	) {
		if (storeOwnerId == null) {
			throw new IllegalArgumentException("storeOwnerId is required");
		}
		return ownerSettlementQueryRepository.findOwnerSettlements(storeOwnerId, status, from, to, pageable);
	}

	@Transactional
	@Override
	public Long createSettlement(Long storeOwnerId, LocalDate periodStart, LocalDate periodEnd) {
		if (storeOwnerId == null) {
			throw new IllegalArgumentException("storeOwnerId is required");
		}
		if (periodStart == null || periodEnd == null) {
			throw new IllegalArgumentException("periodStart/periodEnd are required");
		}
		if (periodEnd.isBefore(periodStart)) {
			throw new IllegalArgumentException("periodEnd must be >= periodStart");
		}

		StoreOwner storeOwner = storeOwnerRepository.findById(storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("사업자 정보를 찾을 수 없습니다. storeOwnerId=" + storeOwnerId));

		LocalDateTime fromAt = periodStart.atStartOfDay();
		LocalDateTime toAt = periodEnd.plusDays(1).atStartOfDay();

		List<OwnerSettlementItem> items = ownerSettlementItemRepository
				.findByStoreOwner_StoreOwnerIdAndOwnerSettlementIsNullAndOwnerSettlementItemStatusAndPaymentPaidAtBetween(
						storeOwnerId,
						OwnerSettlementItemStatus.PENDING,
						fromAt,
						toAt
				);

		long grossSum = 0;
		long feeSum = 0;
		long netSum = 0;
		for (OwnerSettlementItem item : items) {
			grossSum += item.getGrossAmount();
			feeSum += (item.getPlatformFeeAmount() + item.getPgFeeAmount());
			netSum += item.getNetAmount();
		}

		OwnerSettlement settlement = OwnerSettlement.builder()
				.storeOwner(storeOwner)
				.periodStart(periodStart)
				.periodEnd(periodEnd)
				.totalGrossAmount(grossSum)
				.totalFeeAmount(feeSum)
				.totalNetAmount(netSum)
				.ownerSettlementStatus(OwnerSettlementStatus.CALCULATED)
				.build();

		OwnerSettlement saved = ownerSettlementRepository.save(settlement);

		for (OwnerSettlementItem item : items) {
			item.includeToSettlement(saved);
		}
		ownerSettlementItemRepository.saveAll(items);

		return saved.getOwnerSettlementId();
	}

	@Transactional
	@Override
	public void payoutSettlement(Long storeOwnerId, Long ownerSettlementId, String portoneTransferId) {
		if (storeOwnerId == null || ownerSettlementId == null) {
			throw new IllegalArgumentException("storeOwnerId/ownerSettlementId are required");
		}

		OwnerSettlement settlement = ownerSettlementRepository
				.findByOwnerSettlementIdAndStoreOwner_StoreOwnerId(ownerSettlementId, storeOwnerId)
				.orElseThrow(() -> new IllegalArgumentException("정산 건을 찾을 수 없습니다. ownerSettlementId=" + ownerSettlementId));

		if (settlement.getOwnerSettlementStatus() == OwnerSettlementStatus.PAID) {
			return;
		}

		settlement.markPaid(portoneTransferId);
		ownerSettlementRepository.save(settlement);

		List<OwnerSettlementItem> items = ownerSettlementItemRepository.findByOwnerSettlement_OwnerSettlementId(ownerSettlementId);
		for (OwnerSettlementItem item : items) {
			item.markPaid();
		}
		ownerSettlementItemRepository.saveAll(items);

		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.STORE_PAYOUT)
				.relatedEntityId(ownerSettlementId)
				.relatedEntityType("OWNER_SETTLEMENT")
				.amount(settlement.getTotalNetAmount())
				.currency("KRW")
				.transactionStatus(PaymentConstant.TRANSACTION_STATUS_COMPLETED)
				.portoneTransferId(portoneTransferId)
				.createdAt(LocalDateTime.now())
				.completedAt(LocalDateTime.now())
				.build();

		transactionService.saveTransaction(transaction);
	}
}
