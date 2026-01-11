package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class DelivererPaymentDTO {

    // 자동 증가
    private Long delivererPaymentId;

    @NotNull
    private Long delivererId;

    @NotNull
    @PositiveOrZero
    private Long delivererPaymentMinimumFee;
    
    // Null 가능
    @PositiveOrZero
    private Long delivererPaymentDistanceFee;

}
