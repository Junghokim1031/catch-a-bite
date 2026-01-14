package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.OrderDeliveryDTO;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.entity.StoreOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderDeliveryConverter {

    // dto의 Long 타입 delivererId와 entity의 Deliverer 타입 deliverer를 연결시키기 위해 사용, orderId도 같은 방법(orderId)
    // -> getReference를 이용하기 위해 필요 -> getReference는 Delivere (entity)에서 delivererId에 접근할 수 있게하는 메서드
    @PersistenceContext
    private EntityManager em;

    public OrderDeliveryDTO toDto(OrderDelivery entity) {
        if (entity == null) return null;

        OrderDeliveryDTO orderDeliveryDTO = OrderDeliveryDTO.builder()
            .deliveryId(entity.getDeliveryId())
            .orderId(entity.getStoreOrder().getOrderId())
            .delivererId(entity.getDeliverer().getDelivererId())
            .orderAcceptTime(entity.getOrderAcceptTime())
            .orderDeliveryPickupTime(entity.getOrderDeliveryPickupTime())
            .orderDeliveryStartTime(entity.getOrderDeliveryStartTime())
            .orderDeliveryCompleteTime(entity.getOrderDeliveryCompleteTime())
            .orderDeliveryDistance(entity.getOrderDeliveryDistance())
            .build();

        return orderDeliveryDTO;
    }

    public OrderDelivery toEntity(OrderDeliveryDTO dto) {
        if (dto == null) return null;

        Deliverer delivererRef = em.getReference(Deliverer.class, dto.getDelivererId());
        StoreOrder storeOrderRef = em.getReference(StoreOrder.class,dto.getOrderId());
        
        OrderDelivery orderDelivery = OrderDelivery.builder()
            .deliveryId(dto.getDeliveryId())
            .storeOrder(storeOrderRef)
            .deliverer(delivererRef)
            .orderAcceptTime(dto.getOrderAcceptTime())
            .orderDeliveryPickupTime(dto.getOrderDeliveryPickupTime())
            .orderDeliveryStartTime(dto.getOrderDeliveryStartTime())
            .orderDeliveryCompleteTime(dto.getOrderDeliveryCompleteTime())
            .orderDeliveryDistance(dto.getOrderDeliveryDistance())
            .build();
        
        return orderDelivery;

    }
}
