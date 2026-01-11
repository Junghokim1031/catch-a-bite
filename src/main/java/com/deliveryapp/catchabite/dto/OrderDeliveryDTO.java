package com.deliveryapp.catchabite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long delivererId;

    private LocalDateTime orderAcceptTime;
    private LocalDateTime orderDeliveryPickupTime;
    private LocalDateTime orderDeliveryStartTime;
    private LocalDateTime orderDeliveryCompleteTime;
    
    private BigDecimal orderDeliveryDistance;

    private Integer orderDeliveryEstTime;
    private Integer orderDeliveryActTime;
    
    private DeliveryStatus orderDeliveryStatus;
    private LocalDateTime orderDeliveryCreatedDate;

}
