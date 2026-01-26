package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.dto.OwnerOrderDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerOrderServiceImpl implements OwnerOrderService {

    private final StoreRepository storeRepository;
    private final StoreOrderRepository storeOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OwnerOrderDTO> listOrders(
            Long storeOwnerId,
            Long storeId,
            String status,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        validateOwnedStore(storeOwnerId, storeId);

        boolean hasDateFilter = (from != null || to != null);
        LocalDateTime fromDt = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toDt = (to == null) ? null : to.plusDays(1).atStartOfDay().minusNanos(1);

        Page<StoreOrder> page;

        if (status == null || status.isBlank()) {
            if (hasDateFilter) {
                if (fromDt == null) fromDt = LocalDate.of(1970, 1, 1).atStartOfDay();
                if (toDt == null) toDt = LocalDateTime.now().plusYears(100);
                page = storeOrderRepository.findByStore_StoreIdAndOrderDateBetween(storeId, fromDt, toDt, pageable);
            } else {
                page = storeOrderRepository.findByStore_StoreId(storeId, pageable);
            }
        } else {
            OrderStatus orderStatus = parseStatus(status);

            if (hasDateFilter) {
                if (fromDt == null) fromDt = LocalDate.of(1970, 1, 1).atStartOfDay();
                if (toDt == null) toDt = LocalDateTime.now().plusYears(100);
                page = storeOrderRepository.findByStore_StoreIdAndOrderStatusAndOrderDateBetween(
                        storeId, orderStatus, fromDt, toDt, pageable
                );
            } else {
                page = storeOrderRepository.findByStore_StoreIdAndOrderStatus(storeId, orderStatus, pageable);
            }
        }

        Page<OwnerOrderDTO> dtoPage = page.map(o -> OwnerOrderDTO.builder()
                .orderId(o.getOrderId())
                .storeId(o.getStore().getStoreId())
                .orderStatus(o.getOrderStatus() == null ? null : o.getOrderStatus().getValue())
                .orderCreatedAt(o.getOrderDate())
                .orderTotalPrice(o.getOrderTotalPrice())
                .deliveryFee(o.getOrderDeliveryFee())
                .orderAddress(o.getOrderAddressSnapshot())
                .build());

        return PageResponseDTO.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerOrderDTO getOrderDetail(Long storeOwnerId, Long storeId, Long orderId) {

        validateOwnedStore(storeOwnerId, storeId);

        StoreOrder order = storeOrderRepository.findDetailByOrderIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));

        List<OwnerOrderDTO.ItemDTO> items = (order.getOrderItems() == null)
                ? Collections.emptyList()
                : order.getOrderItems().stream()
                .map(oi -> OwnerOrderDTO.ItemDTO.builder()
                        .orderItemId(oi.getOrderItemId())
                        .menuId(null) // 현재 OrderItem에는 menu_id가 없으므로 null
                        .menuName(oi.getOrderItemName())
                        .menuPrice(oi.getOrderItemPrice())
                        .quantity(oi.getOrderItemQuantity())
                        .options(oi.getOrderOptions() == null ? Collections.emptyList()
                                : oi.getOrderOptions().stream()
                                .map(oo -> OwnerOrderDTO.OptionDTO.builder()
                                        .orderOptionId(oo.getOrderOptionId())
                                        .menuOptionGroupId(null)
                                        .menuOptionGroupName(null)
                                        .menuOptionId(null)
                                        .menuOptionName(oo.getOrderOptionName())
                                        .menuOptionPrice(oo.getOrderOptionExtraPrice() == null ? 0L : oo.getOrderOptionExtraPrice())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return OwnerOrderDTO.builder()
                .orderId(order.getOrderId())
                .storeId(order.getStore().getStoreId())
                .orderStatus(order.getOrderStatus() == null ? null : order.getOrderStatus().getValue())
                .orderCreatedAt(order.getOrderDate())
                .orderTotalPrice(order.getOrderTotalPrice())
                .deliveryFee(order.getOrderDeliveryFee())
                .orderAddress(order.getOrderAddressSnapshot())
                .items(items)
                .build();
    }

    @Override
    public void acceptOrder(Long storeOwnerId, Long storeId, Long orderId) {

        StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("invalid order status");
        }

        order.changeStatus(OrderStatus.COOKING);
    }

    @Override
    public void rejectOrder(Long storeOwnerId, Long storeId, Long orderId, String reason) {

        StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("invalid order status");
        }

        order.changeStatus(OrderStatus.REJECTED);
    }

    @Override
    public void markCooked(Long storeOwnerId, Long storeId, Long orderId) {

        StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

        if (order.getOrderStatus() != OrderStatus.COOKING) {
            throw new IllegalArgumentException("invalid order status");
        }

        order.changeStatus(OrderStatus.COOKED);
    }

    @Override
    public void markDelivered(Long storeOwnerId, Long storeId, Long orderId) {

        StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

        if (order.getOrderStatus() != OrderStatus.COOKED) {
            throw new IllegalArgumentException("invalid order status");
        }

        order.changeStatus(OrderStatus.DELIVERED);
    }

    // -------------------------
    // private helpers
    // -------------------------

    private void validateOwnedStore(Long storeOwnerId, Long storeId) {
        boolean owned = storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId);
        if (!owned) {
            throw new IllegalArgumentException("not your store");
        }
    }

    private StoreOrder getOwnedOrder(Long storeOwnerId, Long storeId, Long orderId) {

        validateOwnedStore(storeOwnerId, storeId);

        return storeOrderRepository.findByOrderIdAndStore_StoreId(orderId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
    }

    private OrderStatus parseStatus(String status) {
        try {
            return OrderStatus.from(status);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid status");
        }
    }
}
