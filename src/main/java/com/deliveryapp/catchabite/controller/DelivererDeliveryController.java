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
@RequestMapping("/api/v1/rider/deliveries")
@RequiredArgsConstructor
public class DelivererDeliveryController {

    private final OrderDeliveryService deliveryService;

    // 내 배달 단건 조회 (배달원)
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryApiResponseDTO<OrderDeliveryDTO>> getDelivery(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveryForDeliverer(deliveryId, user.getDelivererId())
            )
        );
    }

    // 내 배달 목록 (배달원)
    @GetMapping
    public ResponseEntity<DeliveryApiResponseDTO<List<OrderDeliveryDTO>>> getMyDeliveries(
            @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveriesByDeliverer(user.getDelivererId())
            )
        );
    }

    // 상태별 내 배달 조회 (배달원)
    @GetMapping("/status")
    public ResponseEntity<DeliveryApiResponseDTO<List<OrderDeliveryDTO>>> getByStatus(
            @AuthenticationPrincipal AuthUser user,
            @RequestParam DeliveryStatus orderDeliveryStatus
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveriesByDelivererInStatus(user.getDelivererId(), orderDeliveryStatus)
            )
        );
    }

}
