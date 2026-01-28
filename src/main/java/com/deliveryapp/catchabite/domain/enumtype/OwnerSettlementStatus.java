package com.deliveryapp.catchabite.domain.enumtype;

/**
 * 사업자 정산(기간별) 상태
 */
public enum OwnerSettlementStatus {
	CALCULATED, // 기간 내 정산 집계 완료(지급 전)
	PAID,       // 지급 완료
	CANCELED    // 정산 취소(환불/정산 오류 등)
}
