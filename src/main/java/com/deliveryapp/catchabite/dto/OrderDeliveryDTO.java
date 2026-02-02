package com.deliveryapp.catchabite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOrder;

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

    private Long delivererId;

    private LocalDateTime orderAcceptTime;
    private LocalDateTime orderDeliveryPickupTime;
    private LocalDateTime orderDeliveryStartTime;
    private LocalDateTime orderDeliveryCompleteTime;

    private BigDecimal orderDeliveryDistance;

    private Integer orderDeliveryEstTime;
    
    private Integer orderDeliveryActTime;
    
    // 기본 상태를 PENDING(대기)로 설정
    @Builder.Default
    private DeliveryStatus orderDeliveryStatus = DeliveryStatus.PENDING;

    // StoreOrder.orderStatus (조리/주문 진행 상태)
    private OrderStatus orderStatus;

    private LocalDateTime orderDeliveryCreatedDate;

    /********************************** 01/26일 추가 **************************************/
    // 매장 좌표
    private BigDecimal storeLatitude;
    private BigDecimal storeLongitude;
    // 고객 좌표
    private BigDecimal dropoffLatitude;
    private BigDecimal dropoffLongitude;
    /*************************************************************************************/

    // UI 표시용
    private String storeName;        // 가게이름 (Store.storeName)
    private String storeAddress;     // 가게주소 (Store.storeAddress)
    private String dropoffAddress;   // 배달주소 (StoreOrder.orderAddressSnapshot)
    private Long orderDeliveryFee;   // 배달비 (StoreOrder.orderDeliveryFee)

    /********************************** 01/19일 추가 **************************************/
    // OrderDelivery의 실제 모든 필드를 찾아온다.
    public static OrderDeliveryDTO from(OrderDelivery od) {
        if (od == null) return null;

        StoreOrder so = od.getStoreOrder();
        Store store = (so != null) ? so.getStore() : null;

        return OrderDeliveryDTO.builder()
                .deliveryId(od.getDeliveryId())
                .orderId(so != null ? so.getOrderId() : null)
                .delivererId(od.getDeliverer() != null ? od.getDeliverer().getDelivererId() : null)

                // 기존 필드들(필요하면 유지)
                .orderAcceptTime(od.getOrderAcceptTime())
                .orderDeliveryPickupTime(od.getOrderDeliveryPickupTime())
                .orderDeliveryStartTime(od.getOrderDeliveryStartTime())
                .orderDeliveryCompleteTime(od.getOrderDeliveryCompleteTime())
                .orderDeliveryEstTime(od.getOrderDeliveryEstTime()) // 시간 null 허용
                .orderDeliveryActTime(od.getOrderDeliveryActTime()) // 시간 null 허용
                .orderDeliveryStatus(od.getOrderDeliveryStatus())
                .orderDeliveryCreatedDate(od.getOrderDeliveryCreatedDate())

                // UI 표시용 (여기가 핵심)
                .storeName(store != null ? store.getStoreName() : null)
                .storeAddress(store != null ? store.getStoreAddress() : null)
                .dropoffAddress(so != null ? so.getOrderAddressSnapshot() : null)
                .orderDeliveryFee(so != null ? so.getOrderDeliveryFee() : null)
                .orderStatus(od.getStoreOrder() != null ? od.getStoreOrder().getOrderStatus() : null)

                .build();
        }      
    /*************************************************************************************/

}
