package com.deliveryapp.catchabite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeliveryDTO {

    private Long deliveryId;

    @NotNull
    private Long orderId;

    private Long delivererId;

    private LocalDateTime orderAcceptTime;
    private LocalDateTime orderDeliveryPickupTime;
    private LocalDateTime orderDeliveryStartTime;
    private LocalDateTime orderDeliveryCompleteTime;

    @PositiveOrZero
    private BigDecimal orderDeliveryDistance;

    @PositiveOrZero
    private Integer orderDeliveryEstTime;
    
    @PositiveOrZero
    private Integer orderDeliveryActTime;
    
    // 기본 상태를 PENDING(대기)로 설정
    @Builder.Default
    private DeliveryStatus orderDeliveryStatus = DeliveryStatus.PENDING;

    private LocalDateTime orderDeliveryCreatedDate;

}
