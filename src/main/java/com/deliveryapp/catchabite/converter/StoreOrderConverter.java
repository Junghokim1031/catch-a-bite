package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreOrderConverter {

    public StoreOrderDTO toDto(StoreOrder entity) {
        if (entity == null) return null;
        
        return StoreOrderDTO.builder()
                .orderId(entity.getOrderId())
                .appUserId(entity.getAppUser()!= null?entity.getAppUser().getAppUserId() : null)
                .storeId(entity.getStore() != null ? entity.getStore().getStoreId() : null)
                .addressId(entity.getAddress() != null ? entity.getAddress().getAddressId() : null)
                .orderAddressSnapshot(entity.getOrderAddressSnapshot())
                .orderTotalPrice(entity.getOrderTotalPrice())
                .orderDeliveryFee(entity.getOrderDeliveryFee())
                .orderStatus(entity.getOrderStatus())
                .orderDate(entity.getOrderDate())
                .paymentId(getPaymentId(entity))
                .reviewId(getReviewId(entity))
                .orderDeliveryId(getOrderDeliveryId(entity))
                .build();
    }
    
    public StoreOrder toEntity(StoreOrderDTO dto, AppUser appUser, Store store, Address address) {
        if (dto == null) return null;
        
        return StoreOrder.builder()
                .orderId(dto.getOrderId())
                .appUser(appUser)
                .store(store) 
                .address(address)
                .orderAddressSnapshot(dto.getOrderAddressSnapshot())
                .orderTotalPrice(dto.getOrderTotalPrice())
                .orderDeliveryFee(dto.getOrderDeliveryFee())
                .orderStatus(dto.getOrderStatus())
                .orderDate(dto.getOrderDate())
                .build();
    }

    // Safe ID extraction helpers
    private Long getPaymentId(StoreOrder entity) {
        return entity.getPayment() != null ? entity.getPayment().getPaymentId() : null;
    }

    private Long getReviewId(StoreOrder entity) {
        return entity.getReview() != null ? entity.getReview().getReviewId() : null;
    }

    private Long getOrderDeliveryId(StoreOrder entity) {
        return entity.getOrderDelivery() != null ? entity.getOrderDelivery().getDeliveryId() : null;
    }
}
