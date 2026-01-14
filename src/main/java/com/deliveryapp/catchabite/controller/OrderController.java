package com.deliveryapp.catchabite.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;

@RestController
@RequestMapping("/api")
public class OrderController {
    
    @Autowired
    private StoreOrderRepository storeOrderRepository;
    
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderData(@PathVariable Long orderId) {
        StoreOrder order = storeOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        return ResponseEntity.ok(Map.of(
            "orderId", order.getOrderId(),
            "appUserId", order.getAppUser().getAppUserId(),
            "userName", order.getAppUser().getAppUserName(),
            "userEmail", order.getAppUser().getAppUserEmail(),
            "userPhone", order.getAppUser().getAppUserMobile(),
            "totalPrice", order.getOrderTotalPrice(),
            "address", order.getOrderAddressSnapshot(),
            "status", order.getOrderStatus()
        ));
    }
}
