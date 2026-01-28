package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.OrderDeliveryDTO;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.entity.StoreOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class OrderDeliveryConverter {

    @PersistenceContext
    private EntityManager em;

    public OrderDeliveryDTO toDto(OrderDelivery entity) {
        if (entity == null) return null;

        return OrderDeliveryDTO.builder()
            .deliveryId(entity.getDeliveryId())
            .orderId(entity.getStoreOrder() != null ? entity.getStoreOrder().getOrderId() : null)

            // ✅ 배정 전에는 deliverer가 null 가능
            .delivererId(entity.getDeliverer() != null ? entity.getDeliverer().getDelivererId() : null)

            .orderAcceptTime(entity.getOrderAcceptTime())
            .orderDeliveryPickupTime(entity.getOrderDeliveryPickupTime())
            .orderDeliveryStartTime(entity.getOrderDeliveryStartTime())
            .orderDeliveryCompleteTime(entity.getOrderDeliveryCompleteTime())

            .orderDeliveryDistance(entity.getOrderDeliveryDistance())
            .orderDeliveryEstTime(entity.getOrderDeliveryEstTime())
            .orderDeliveryActTime(entity.getOrderDeliveryActTime())

            .orderDeliveryStatus(entity.getOrderDeliveryStatus())
            .orderDeliveryCreatedDate(entity.getOrderDeliveryCreatedDate())

            // ✅ 01/26 추가된 위도/경도
            .storeLatitude(entity.getStoreLatitude())
            .storeLongitude(entity.getStoreLongitude())
            .dropoffLatitude(entity.getDropoffLatitude())
            .dropoffLongitude(entity.getDropoffLongitude())
            .build();
    }

    public OrderDelivery toEntity(OrderDeliveryDTO dto) {
        if (dto == null) return null;

        // ✅ storeOrder는 optional=false 이므로 orderId는 필수(DTO에서도 @NotNull)
        StoreOrder storeOrderRef = em.getReference(StoreOrder.class, dto.getOrderId());

        // ✅ deliverer는 배정 전 null 가능
        Deliverer delivererRef = (dto.getDelivererId() == null)
            ? null
            : em.getReference(Deliverer.class, dto.getDelivererId());

        return OrderDelivery.builder()
            .deliveryId(dto.getDeliveryId())
            .storeOrder(storeOrderRef)
            .deliverer(delivererRef)

            .orderAcceptTime(dto.getOrderAcceptTime())
            .orderDeliveryPickupTime(dto.getOrderDeliveryPickupTime())
            .orderDeliveryStartTime(dto.getOrderDeliveryStartTime())
            .orderDeliveryCompleteTime(dto.getOrderDeliveryCompleteTime())

            .orderDeliveryDistance(dto.getOrderDeliveryDistance())
            .orderDeliveryEstTime(dto.getOrderDeliveryEstTime())
            .orderDeliveryActTime(dto.getOrderDeliveryActTime())

            .orderDeliveryStatus(dto.getOrderDeliveryStatus())
            .orderDeliveryCreatedDate(dto.getOrderDeliveryCreatedDate())

            // ✅ 01/26 추가된 위도/경도
            .storeLatitude(dto.getStoreLatitude())
            .storeLongitude(dto.getStoreLongitude())
            .dropoffLatitude(dto.getDropoffLatitude())
            .dropoffLongitude(dto.getDropoffLongitude())
            .build();
    }
}
