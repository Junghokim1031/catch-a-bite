package com.deliveryapp.catchabite.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDTO {
    private Long cartId;
    private Long storeId;
    private String storeName;
    private Long deliveryCost;
    private Long minOrderPrice;
    private List<CartItemDTO> items;
    private Long totalFoodPrice;
}
