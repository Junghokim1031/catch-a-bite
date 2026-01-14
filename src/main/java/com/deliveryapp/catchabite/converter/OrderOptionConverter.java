package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.OrderOptionDTO;
import com.deliveryapp.catchabite.entity.OrderOption;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderOptionConverter {

    public OrderOptionDTO toDto(OrderOption entity) {
        if (entity == null) return null;

        OrderOptionDTO orderOptionDTO = OrderOptionDTO.builder()
            .orderOptionId(entity.getOrderOptionId())
            .orderItemId(entity.getOrderItem().getOrderItemId())
            .orderOptionName(entity.getOrderOptionName())
            .orderOptionExtraPrice(entity.getOrderOptionExtraPrice())
            .build();

        return orderOptionDTO;
    }

    public OrderOption toEntityWithoutParent(OrderOptionDTO dto) {
        if (dto == null) return null;

        OrderOption orderOption = OrderOption.builder()
            .orderOptionId(dto.getOrderOptionId())
            .orderOptionName(dto.getOrderOptionName())
            .orderOptionExtraPrice(dto.getOrderOptionExtraPrice())
            .build();

        return orderOption;    
    }
}
