package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerOrderDTO;

import java.util.List;

public interface OwnerOrderService {

	List<OwnerOrderDTO> listOrders(Long storeOwnerId, Long storeId, String status);

	OwnerOrderDTO getOrderDetail(Long storeOwnerId, Long storeId, Long orderId);

	void acceptOrder(Long storeOwnerId, Long storeId, Long orderId);

	void rejectOrder(Long storeOwnerId, Long storeId, Long orderId, String reason);

	void markCooked(Long storeOwnerId, Long storeId, Long orderId);

	void markDelivered(Long storeOwnerId, Long storeId, Long orderId);
}
