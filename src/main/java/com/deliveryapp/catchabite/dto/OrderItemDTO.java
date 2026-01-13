package com.deliveryapp.catchabite.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;

    @NotNull
    private Long orderId;

    @NotBlank
    @Size(max = 100)
    private String orderItemName;

    @NotNull
    @PositiveOrZero
    private Long orderItemPrice;
    
    // 수량은 보통 0이상 또는 일반적으로 1이상
    @NotNull
    @Positive
    private Long orderItemQuantity;
    
    // 주문 품목 목록
    private List<OrderOptionDTO> orderOptions;

}
