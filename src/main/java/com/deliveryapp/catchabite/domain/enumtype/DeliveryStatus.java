package com.deliveryapp.catchabite.domain.enumtype;

// 주문 배송 상태
// PENDING : 대기, ASSIGNED : 배차 할당
public enum DeliveryStatus { 
    // 대기
    PENDING,
    // 배차요청/배정(수락 대기) 
    ASSIGNED,
    // 수락 완료 
    ACCEPTED,
    PICKED_UP, 
    IN_DELIVERY, 
    DELIVERED, 
    CANCELLED 
}
