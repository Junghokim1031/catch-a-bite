package com.deliveryapp.catchabite.entity;

import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "owner_settlement",
		indexes = {
				@Index(name = "idx_owner_settlement_store_owner_id", columnList = "store_owner_id"),
				@Index(name = "idx_owner_settlement_period", columnList = "period_start, period_end")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OwnerSettlement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "owner_settlement_id", nullable = false)
	private Long ownerSettlementId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_owner_id", nullable = false)
	private StoreOwner storeOwner;

	@Column(name = "period_start", nullable = false)
	private LocalDate periodStart;

	@Column(name = "period_end", nullable = false)
	private LocalDate periodEnd;

	@Column(name = "total_gross_amount", nullable = false)
	private Long totalGrossAmount;

	@Column(name = "total_fee_amount", nullable = false)
	private Long totalFeeAmount;

	@Column(name = "total_net_amount", nullable = false)
	private Long totalNetAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "owner_settlement_status", nullable = false, length = 30)
	private OwnerSettlementStatus ownerSettlementStatus;

	@Column(name = "portone_transfer_id", length = 255)
	private String portoneTransferId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (ownerSettlementStatus == null) {
			ownerSettlementStatus = OwnerSettlementStatus.CALCULATED;
		}
		if (totalGrossAmount == null) totalGrossAmount = 0L;
		if (totalFeeAmount == null) totalFeeAmount = 0L;
		if (totalNetAmount == null) totalNetAmount = 0L;
	}

	public void markPaid(String portoneTransferId) {
		this.ownerSettlementStatus = OwnerSettlementStatus.PAID;
		this.paidAt = LocalDateTime.now();
		this.portoneTransferId = portoneTransferId;
	}
}
