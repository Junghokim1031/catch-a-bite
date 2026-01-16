package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.entity.StoreOrder;

import java.util.List;

public interface UserStoreOrderService {

    // CRUD
    StoreOrderDTO createStoreOrder(StoreOrderDTO dto);
    StoreOrderDTO getStoreOrder(Long orderId);
    List<StoreOrderDTO> getAllStoreOrders();
    StoreOrderDTO updateStoreOrder(Long orderId, StoreOrderDTO dto);
    boolean deleteStoreOrder(Long orderId);
    
    // Review에서 필요한 자료
    StoreOrder getValidatedOrder(Long storeOrderId);
    Long getStoreId(Long storeOrderId);
    Long getAddressId(Long storeOrderId);
}
