package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.service.UserStoreOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appuser/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserStoreOrderService userStoreOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoreOrderDTO>> createOrder(@RequestBody StoreOrderDTO dto) {
        StoreOrderDTO createdOrder = userStoreOrderService.createStoreOrder(dto);
        return ResponseEntity.ok(ApiResponse.ok(createdOrder));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> getOrder(@PathVariable Long orderId) {
        StoreOrderDTO order = userStoreOrderService.getStoreOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreOrderDTO>>> getAllOrders() {
        List<StoreOrderDTO> orders = userStoreOrderService.getAllStoreOrders();
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> updateOrder(@PathVariable Long orderId, @RequestBody StoreOrderDTO dto) {
        StoreOrderDTO updatedOrder = userStoreOrderService.updateStoreOrder(orderId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedOrder));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        boolean deleted = userStoreOrderService.deleteStoreOrder(orderId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.okMessage("Order cancelled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("CANCEL_FAILED", "Failed to cancel order"));
        }
    }
}