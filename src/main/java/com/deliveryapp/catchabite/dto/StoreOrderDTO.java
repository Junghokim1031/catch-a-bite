package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StoreOrderDTO {

    private Long orderId;                   // PK
    private Long appUserId;                 // FK
    private Long storeId;                   // FK
    private Long addressId;                 // FK
    private String storeName;
    private List<OrderItemDTO> orderItems;

    private String orderAddressSnapshot;
    private Long orderTotalPrice;
    private Long orderDeliveryFee;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    private Long paymentId;                 // FK - DB에 없음
    private Long reviewId;                  // FK - DB에 없음
    private Long orderDeliveryId;           // FK - DB에 없음

    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
