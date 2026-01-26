package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerOrderDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OwnerOrderService {

    // ✅ 기존 List 반환 대신, 페이지 응답으로 변경
    PageResponseDTO<OwnerOrderDTO> listOrders(
            Long storeOwnerId,
            Long storeId,
            String status,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    );

    OwnerOrderDTO getOrderDetail(Long storeOwnerId, Long storeId, Long orderId);

    void acceptOrder(Long storeOwnerId, Long storeId, Long orderId);

    void rejectOrder(Long storeOwnerId, Long storeId, Long orderId, String reason);

    void markCooked(Long storeOwnerId, Long storeId, Long orderId);

    void markDelivered(Long storeOwnerId, Long storeId, Long orderId);
}
