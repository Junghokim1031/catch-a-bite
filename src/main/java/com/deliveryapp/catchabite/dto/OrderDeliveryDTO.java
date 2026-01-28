package com.deliveryapp.catchabite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.entity.OrderDelivery;

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

    /********************************** 01/26일 추가 **************************************/
    // 매장 좌표
    private BigDecimal storeLatitude;
    private BigDecimal storeLongitude;
    // 고객 좌표
    private BigDecimal dropoffLatitude;
    private BigDecimal dropoffLongitude;
    /*************************************************************************************/
    /********************************** 01/19일 추가 **************************************/
    // deliveryId와 orderDeliveryStatus(배달 상태)를 찾아온다.
    public static OrderDeliveryDTO from(OrderDelivery od) {
        return OrderDeliveryDTO.builder()
                .deliveryId(od.getDeliveryId())
                .orderDeliveryStatus(od.getOrderDeliveryStatus())
                .build();
    }
    /*************************************************************************************/

}
