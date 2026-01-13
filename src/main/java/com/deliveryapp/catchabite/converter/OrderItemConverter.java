package com.deliveryapp.catchabite.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.OrderItemDTO;
import com.deliveryapp.catchabite.entity.OrderItem;
import com.deliveryapp.catchabite.entity.OrderOption;
import com.deliveryapp.catchabite.entity.StoreOrder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemConverter {

    // dto의 Long 타입 orderId와 entity의 storeOrder를 연결시키기 위해 사용
    // -> getReference를 이용하기 위해 필요 -> getReference는 StoreOrder (entity)에서 orderId에 접근할 수 있게하는 메서드
    @PersistenceContext
    private EntityManager em;

    private final OrderOptionConverter orderOptionConverter;

    public OrderItemDTO toDto(OrderItem entity) {
        if (entity == null) return null;

        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
            .orderItemId(entity.getOrderItemId())
            .orderId(entity.getStoreOrder().getOrderId())
            .orderItemName(entity.getOrderItemName())
            .orderItemPrice(entity.getOrderItemPrice())
            .orderItemQuantity(entity.getOrderItemQuantity())
            .orderOptions(entity.getOrderOptions() == null ? List.of()
            : entity.getOrderOptions().stream()
                .map(orderOptionConverter::toDto)
                .toList())
            .build();
    
        return orderItemDTO;

   }

   public OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) return null;

        OrderItem orderItem = OrderItem.builder()
            .orderItemId(dto.getOrderItemId())
            .storeOrder(em.getReference(StoreOrder.class, dto.getOrderId()))
            .orderItemName(dto.getOrderItemName())
            .orderItemPrice(dto.getOrderItemPrice())
            .orderItemQuantity(dto.getOrderItemQuantity())
            .build();

        // 옵션 리스트 연결 (양방향 세팅)
        if (dto.getOrderOptions() != null) {
            dto.getOrderOptions().forEach(optDto -> {
                OrderOption opt = orderOptionConverter.toEntityWithoutParent(optDto);
                // opt.setOrderItem(orderItem)까지 자동 처리
                orderItem.addOrderOption(opt);
            });
        }

        return orderItem;
   }
    
}
