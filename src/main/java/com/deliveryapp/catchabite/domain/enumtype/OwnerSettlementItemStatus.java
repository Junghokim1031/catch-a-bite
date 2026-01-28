package com.deliveryapp.catchabite.domain.enumtype;

/**
 * 사업자 정산(주문별 라인) 상태
 */
public enum OwnerSettlementItemStatus {
	PENDING,   // 결제 완료 후 정산 대기(기간 정산에 포함 전)
	INCLUDED,  // 특정 기간 정산에 포함됨(지급 전)
	PAID,      // 지급 완료
	CANCELED   // 취소/환불 등으로 제외
}
