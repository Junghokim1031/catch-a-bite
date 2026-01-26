package com.deliveryapp.catchabite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.dto.DelivererActionRequestDTO;
import com.deliveryapp.catchabite.dto.DeliveryApiResponseDTO;
import com.deliveryapp.catchabite.dto.DeliveryAssignRequestDTO;
import com.deliveryapp.catchabite.service.OrderDeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// DeliveryAssignRequestDTO, DeliveryActionRequestDTO, DeliveryApiResponseDTO 이용
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrderDeliveryService deliveryService;

    // 배정(매장주인)
    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> assign(@PathVariable Long deliveryId, @RequestBody @Valid DeliveryAssignRequestDTO req) {
        deliveryService.assignDeliverer(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달원이 배정되었습니다."));
    }

    // 수락(배달원)
    @PostMapping("/{deliveryId}/accept")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> accept(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.accept(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달 요청을 수락했습니다."));
    }

    // 매장에서 픽업완료(배달원)
    @PostMapping("/{deliveryId}/pickup-complete")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> pickupComplete(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.pickupComplete(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("매장에서 픽업완료헀습니다."));
    }

    // 배달시작(배달원)
    @PostMapping("/{deliveryId}/start")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> start(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.startDelivery(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 시작되었습니다."));
    }

    // 배달완료(배달원)
    @PostMapping("/{deliveryId}/complete")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> complete(@PathVariable Long deliveryId, @RequestBody @Valid DelivererActionRequestDTO req) {
        deliveryService.completeDelivery(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 완료되었습니다."));
    }

    // 배달 재오픈(관리자/시스템)
    @PostMapping("/{deliveryId}/reopen")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> reopenDelivery(@PathVariable Long deliveryId) {
        deliveryService.reopenDelivery(deliveryId);
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 재오픈되었습니다."));
    }

}
