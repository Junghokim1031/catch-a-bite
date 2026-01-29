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

    private static final List<OrderStatus> HIDDEN_STATUSES = List.of(
            OrderStatus.PAYMENTINPROGRESS,
            OrderStatus.PAYMENTFAILED
    );

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
            // [수정] 상태 필터가 없을 경우: '숨김 상태'를 제외한 모든 주문을 조회합니다.
            if (hasDateFilter) {
                if (fromDt == null) fromDt = LocalDate.of(1970, 1, 1).atStartOfDay();
                if (toDt == null) toDt = LocalDateTime.now().plusYears(100);
                
                page = storeOrderRepository.findByStore_StoreIdAndOrderStatusNotInAndOrderDateBetween(
                        storeId, HIDDEN_STATUSES, fromDt, toDt, pageable
                );
            } else {
                page = storeOrderRepository.findByStore_StoreIdAndOrderStatusNotIn(
                        storeId, HIDDEN_STATUSES, pageable
                );
            }
        } else {
            // [수정] 특정 상태 필터가 요청된 경우
            OrderStatus orderStatus = parseStatus(status);

            // 요청된 상태가 숨김 대상이라면 빈 페이지 반환 (또는 예외 처리)
            if (HIDDEN_STATUSES.contains(orderStatus)) {
                return PageResponseDTO.from(Page.empty(pageable));
            }

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

        // 상세 조회 시에도 숨김 상태인 경우 접근 차단
        if (HIDDEN_STATUSES.contains(order.getOrderStatus())) {
            throw new IllegalArgumentException("order not accessible");
        }

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
