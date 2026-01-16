package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.service.UserStoreOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store-orders")
public class StoreOrderController {

    private final UserStoreOrderService storeOrderService;

    public StoreOrderController(UserStoreOrderService storeOrderService) {
        this.storeOrderService = storeOrderService;
    }

    @PostMapping
    public ResponseEntity<StoreOrderDTO> createStoreOrder(@Valid @RequestBody StoreOrderDTO request) {
        StoreOrderDTO created = storeOrderService.createStoreOrder(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<StoreOrderDTO> getStoreOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(storeOrderService.getStoreOrder(orderId));
    }

    @GetMapping
    public ResponseEntity<List<StoreOrderDTO>> getAllStoreOrders() {
        return ResponseEntity.ok(storeOrderService.getAllStoreOrders());
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<StoreOrderDTO> updateStoreOrder(@PathVariable Long orderId, @Valid @RequestBody StoreOrderDTO request) {
        return ResponseEntity.ok(storeOrderService.updateStoreOrder(orderId, request));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteStoreOrder(@PathVariable Long orderId) {
        storeOrderService.deleteStoreOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
