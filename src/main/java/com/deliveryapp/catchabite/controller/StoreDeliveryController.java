package com.deliveryapp.catchabite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.domain.enumtype.DeliveryStatus;
import com.deliveryapp.catchabite.dto.DeliveryApiResponseDTO;
import com.deliveryapp.catchabite.dto.OrderDeliveryDTO;
import com.deliveryapp.catchabite.security.AuthUser;
import com.deliveryapp.catchabite.service.OrderDeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/owner/deliveries")
@RequiredArgsConstructor
public class StoreDeliveryController {

    private final OrderDeliveryService deliveryService;

    // 내 매장 배달 단건 조회
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryApiResponseDTO<OrderDeliveryDTO>> getDelivery(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveryForStore(deliveryId, user.getStoreOwnerId())
            )
        );
    }

    // 내 매장 전체 배달 목록
    @GetMapping
    public ResponseEntity<DeliveryApiResponseDTO<List<OrderDeliveryDTO>>> getDeliveries(
            @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveriesByStore(user.getStoreOwnerId())
            )
        );
    }

    // 상태별 조회
    @GetMapping("/status")
    public ResponseEntity<DeliveryApiResponseDTO<List<OrderDeliveryDTO>>> getByStatus(
            @AuthenticationPrincipal AuthUser user,
            @RequestParam DeliveryStatus orderDeliveryStatus
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveriesByStoreAndStatus(user.getStoreOwnerId(), orderDeliveryStatus)
            )
        );
    }

}
