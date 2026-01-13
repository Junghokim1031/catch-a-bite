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

    // @NotNull 사용 여부 결정하기  -> @NotNull은 null만 허용하지 않고, "", " "(공백)은 허용
    @NotNull
    private Long delivererId;

    // @NotNull 사용 여부 결정하기
    @NotNull
    @PositiveOrZero
    private Long delivererPaymentMinimumFee;
    
    // Null 가능
    @PositiveOrZero
    private Long delivererPaymentDistanceFee;

}
