package com.deliveryapp.catchabite.domain.enumtype;

// 주문 배송 상태
// PENDING : 대기, ASSIGNED : 배차 할당
public enum DeliveryStatus { PENDING, ASSIGNED, PICKED_UP, IN_DELIVERY, DELIVERED, CANCELLED }
