package com.deliveryapp.catchabite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.dto.DeliveryApiResponseDTO;
import com.deliveryapp.catchabite.dto.DeliveryAssignRequestDTO;
import com.deliveryapp.catchabite.security.AuthUser;
import com.deliveryapp.catchabite.service.OrderDeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// ✅ A 방식: 배달원 액션은 delivererId를 Body로 받지 않고, 로그인(Principal)에서 꺼냄
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrderDeliveryService deliveryService;

    // 배정(매장주인)
    // "배달원을 지정(assign)"하는 요청이라 delivererId가 필요하므로 기존처럼 Body 유지
    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> assign(
            @PathVariable Long deliveryId,
            @RequestBody @Valid DeliveryAssignRequestDTO req
            // (@AuthenticationPrincipal AuthUser user 받아서 "매장주인 권한" 검증 가능)
    ) {
        deliveryService.assignDeliverer(deliveryId, req.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달원이 배정되었습니다."));
    }

    // 수락(배달원)
    @PostMapping("/{deliveryId}/accept")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> accept(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        deliveryService.accept(deliveryId, user.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달 요청을 수락했습니다."));
    }

    // 매장에서 픽업완료(배달원)
    @PostMapping("/{deliveryId}/pickup-complete")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> pickupComplete(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        deliveryService.pickupComplete(deliveryId, user.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("매장에서 픽업완료했습니다."));
    }

    // 배달시작(배달원)
    @PostMapping("/{deliveryId}/start")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> start(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        deliveryService.startDelivery(deliveryId, user.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 시작되었습니다."));
    }

    // 배달완료(배달원)
    @PostMapping("/{deliveryId}/complete")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> complete(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        deliveryService.completeDelivery(deliveryId, user.getDelivererId());
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 완료되었습니다."));
    }

    // 배달 재오픈(관리자/시스템)
    @PostMapping("/{deliveryId}/reopen")
    public ResponseEntity<DeliveryApiResponseDTO<Void>> reopenDelivery(
            @PathVariable Long deliveryId
    ) {
        deliveryService.reopenDelivery(deliveryId);
        return ResponseEntity.ok(DeliveryApiResponseDTO.success("배달이 재오픈되었습니다."));
    }
}
