package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.entity.OwnerSettlementItem;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.payment.repository.PaymentRepository;
import com.deliveryapp.catchabite.repository.OwnerSettlementItemRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class OwnerSettlementItemServiceImpl implements OwnerSettlementItemService {

	private final StoreOrderRepository storeOrderRepository;
	private final PaymentRepository paymentRepository;
	private final OwnerSettlementItemRepository ownerSettlementItemRepository;

	/**
	 * 플랫폼 수수료율 (예: 0.10 = 10%)
	 * - 기능/문서에서 확정되지 않은 경우를 대비해 기본값 0
	 */
	@Value("${owner.settlement.platform-fee-rate:0}")
	private double platformFeeRate;

	/**
	 * PG 수수료율 (예: 0.03 = 3%)
	 * - 기능/문서에서 확정되지 않은 경우를 대비해 기본값 0
	 */
	@Value("${owner.settlement.pg-fee-rate:0}")
	private double pgFeeRate;

	@Transactional
	@Override
	public void recordPaidOrder(Long orderId) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId is required");
		}

		if (ownerSettlementItemRepository.existsByStoreOrder_OrderId(orderId)) {
			return; // idempotent
		}

		StoreOrder order = storeOrderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId=" + orderId));

		Payment payment = paymentRepository.findByStoreOrder(order)
				.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. orderId=" + orderId));

		if (!PaymentConstant.PAYMENT_STATUS_PAID.equals(payment.getPaymentStatus())) {
			throw new IllegalStateException("결제 완료 상태가 아닙니다. orderId=" + orderId);
		}

		Long gross = payment.getPaymentAmount();
		Long platformFee = computeFee(gross, platformFeeRate);
		Long pgFee = computeFee(gross, pgFeeRate);
		Long net = gross - platformFee - pgFee;

		OwnerSettlementItem item = OwnerSettlementItem.builder()
				.ownerSettlement(null)
				.storeOwner(order.getStore().getStoreOwner())
				.store(order.getStore())
				.storeOrder(order)
				.payment(payment)
				.paymentPaidAt(payment.getPaymentPaidAt())
				.grossAmount(gross)
				.platformFeeAmount(platformFee)
				.pgFeeAmount(pgFee)
				.netAmount(net)
				.ownerSettlementItemStatus(OwnerSettlementItemStatus.PENDING)
				.build();

		ownerSettlementItemRepository.save(item);
	}

	private Long computeFee(Long amount, double rate) {
		if (amount == null) return 0L;
		if (rate <= 0) return 0L;
		return BigDecimal.valueOf(amount)
				.multiply(BigDecimal.valueOf(rate))
				.setScale(0, RoundingMode.HALF_UP)
				.longValue();
	}
}
