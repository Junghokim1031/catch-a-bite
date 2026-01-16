package com.deliveryapp.catchabite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.dto.DelivererActionRequestDTO;
import com.deliveryapp.catchabite.dto.DeliveryAssignRequestDTO;
import com.deliveryapp.catchabite.service.OrderDeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrderDeliveryService deliveryService;

    // 배정(관리자/시스템)
    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<Void> assign(@PathVariable Long deliveryId, @RequestBody @Valid DeliveryAssignRequestDTO req) {
        deliveryService.assignDeliverer(deliveryId, req.getDelivererId());
        return ResponseEntity.noContent().build();
    }

    // 수락(배달원)
    @PostMapping("/{deliveryId}/accept")
    public ResponseEntity<Void> accept(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.accept(deliveryId, req.getDelivererId());
        return ResponseEntity.noContent().build();
    }

    // 매장에서 픽업완료(배달원)
    @PostMapping("/{deliveryId}/pickup-complete")
    public ResponseEntity<Void> pickupComplete(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.pickupComplete(deliveryId, req.getDelivererId());
        return ResponseEntity.noContent().build();
    }

    // 배달시작(배달원)
    @PostMapping("/{deliveryId}/start")
    public ResponseEntity<Void> start(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.startDelivery(deliveryId, req.getDelivererId());
        return ResponseEntity.noContent().build();
    }

    // 배달완료(배달원)
    @PostMapping("/{deliveryId}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.completeDelivery(deliveryId, req.getDelivererId());
        return ResponseEntity.noContent().build();
    }

    // 배달 재오픈(관리자/시스템)
    @PostMapping("/{deliveryId}/reopen")
    public ResponseEntity<Void> reopenDelivery(@PathVariable Long deliveryId) {
        deliveryService.reopenDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

}
