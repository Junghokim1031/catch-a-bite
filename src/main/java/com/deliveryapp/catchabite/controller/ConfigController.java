package com.deliveryapp.catchabite.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api")
public class ConfigController {
    
    @Value("${portone.store-id}")
    private String storeId;
    
    @GetMapping("/config/store-id")
    public ResponseEntity<?> getStoreId() {
        return ResponseEntity.ok(Map.of(
            "storeId", storeId
        ));
    }
}
