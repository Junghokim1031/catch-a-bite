package com.deliveryapp.catchabite.service;

public interface OwnerSettlementItemService {

	/**
	 * 결제 완료된 주문에 대해 사업자 정산 라인(주문별)을 1회 생성합니다.
	 * (중복 생성 방지)
	 */
	void recordPaidOrder(Long orderId);
}
