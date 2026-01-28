package com.deliveryapp.catchabite.entity;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "owner_settlement_item",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_owner_settlement_item_order_id", columnNames = "order_id")
		},
		indexes = {
				@Index(name = "idx_owner_settlement_item_store_owner_id", columnList = "store_owner_id"),
				@Index(name = "idx_owner_settlement_item_payment_paid_at", columnList = "payment_paid_at"),
				@Index(name = "idx_owner_settlement_item_owner_settlement_id", columnList = "owner_settlement_id")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OwnerSettlementItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "owner_settlement_item_id", nullable = false)
	private Long ownerSettlementItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_settlement_id")
	private OwnerSettlement ownerSettlement;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_owner_id", nullable = false)
	private StoreOwner storeOwner;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private StoreOrder storeOrder;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Column(name = "payment_paid_at", nullable = false)
	private LocalDateTime paymentPaidAt;

	@Column(name = "gross_amount", nullable = false)
	private Long grossAmount;

	@Column(name = "platform_fee_amount", nullable = false)
	private Long platformFeeAmount;

	@Column(name = "pg_fee_amount", nullable = false)
	private Long pgFeeAmount;

	@Column(name = "net_amount", nullable = false)
	private Long netAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "owner_settlement_item_status", nullable = false, length = 30)
	private OwnerSettlementItemStatus ownerSettlementItemStatus;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if (createdAt == null) createdAt = LocalDateTime.now();
		if (ownerSettlementItemStatus == null) ownerSettlementItemStatus = OwnerSettlementItemStatus.PENDING;
		if (platformFeeAmount == null) platformFeeAmount = 0L;
		if (pgFeeAmount == null) pgFeeAmount = 0L;
		if (grossAmount == null) grossAmount = 0L;
		if (netAmount == null) netAmount = 0L;
	}

	public void includeToSettlement(OwnerSettlement settlement) {
		this.ownerSettlement = settlement;
		this.ownerSettlementItemStatus = OwnerSettlementItemStatus.INCLUDED;
	}

	public void markPaid() {
		this.ownerSettlementItemStatus = OwnerSettlementItemStatus.PAID;
	}
}
