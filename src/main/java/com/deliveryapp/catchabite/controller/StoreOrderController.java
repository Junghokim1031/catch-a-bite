package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.dto.UserStoreSummaryDTO;
import com.deliveryapp.catchabite.service.UserStoreOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appuser/store-orders")
@RequiredArgsConstructor
public class StoreOrderController {

    private final UserStoreOrderService storeOrderService;


    /**
     * 주문내역 생성
     * [POST] /api/v1/appuser/store-orders
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StoreOrderDTO>> createStoreOrder(@Valid @RequestBody StoreOrderDTO request) {
        StoreOrderDTO created = storeOrderService.createStoreOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    /**
     * 주문내역 ID로 조회
     * [GET] /api/v1/appuser/store-orders{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> getStoreOrder(@PathVariable Long orderId) {
        StoreOrderDTO order = storeOrderService.getStoreOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    /**
     * 모든 주문내역 조회
     * [GET] /api/v1/appuser/store-orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreOrderDTO>>> getAllStoreOrders() {
        List<StoreOrderDTO> orders = storeOrderService.getAllStoreOrders();
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    /**
     * 특정 사용자의 모든 주문내역 조회
     * [GET] /api/v1/appuser/store-orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StoreOrderDTO>>> getAllStoreOrdersForUser(@PathVariable Long userId) {
        List<StoreOrderDTO> orders = storeOrderService.getAllStoreOrdersForId(userId);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    /**
     * 주문내역 수정
     * [PUT] /api/v1/appuser/store-orders/{orderId}
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> updateStoreOrder(@PathVariable Long orderId, @Valid @RequestBody StoreOrderDTO request) {
        StoreOrderDTO updated = storeOrderService.updateStoreOrder(orderId, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 주문내역 취소
     * [DELETE] /api/v1/appuser/store-orders/{orderId}
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreOrder(@PathVariable Long orderId) {
        boolean deleted = storeOrderService.deleteStoreOrder(orderId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.okMessage("Order cancelled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("CANCEL_FAILED", "Failed to cancel order"));
        }
    }

    /**
     * 자주 방문한 매장 목록 조회 (Top 10)
     * [GET] /api/v1/store-orders/frequent?userId={userId}
     */
    @GetMapping("/frequent")
    public ResponseEntity<ApiResponse<List<UserStoreSummaryDTO>>> getFrequentStores(@RequestParam Long userId) {
        List<UserStoreSummaryDTO> frequentStores = storeOrderService.getFrequentStores(userId);
        return ResponseEntity.ok(ApiResponse.ok(frequentStores));
    }
}