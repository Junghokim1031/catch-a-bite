package com.deliveryapp.catchabite.domain.enumtype;

/* 배달원 - 정산 상태 */
public enum SettlementStatus {
    REQUESTED,  // 정산 요청
    PAID,       // 지급 완료
    REJECTED    // 반려/취소
}
