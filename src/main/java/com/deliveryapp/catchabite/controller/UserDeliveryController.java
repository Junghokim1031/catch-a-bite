package com.deliveryapp.catchabite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.dto.DeliveryApiResponseDTO;
import com.deliveryapp.catchabite.dto.OrderDeliveryDTO;
import com.deliveryapp.catchabite.security.AuthUser;
import com.deliveryapp.catchabite.service.OrderDeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/deliveries")
@RequiredArgsConstructor
public class UserDeliveryController {

    private final OrderDeliveryService deliveryService;

    // 내 배달 단건 조회
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryApiResponseDTO<OrderDeliveryDTO>> getDelivery(
        @PathVariable Long deliveryId,
        @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success("조회 성공",
                deliveryService.getDeliveryForUser(deliveryId, user.getUserId()))
        );
    }

    // 내 주문들의 배달 목록
    @GetMapping
    public ResponseEntity<DeliveryApiResponseDTO<List<OrderDeliveryDTO>>> getMyDeliveries(
            @AuthenticationPrincipal AuthUser user
    ) {
        return ResponseEntity.ok(
            DeliveryApiResponseDTO.success(
                "조회 성공",
                deliveryService.getDeliveriesByUser(user.getUserId())
            )
        );
    }

}
