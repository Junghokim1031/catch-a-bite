package com.deliveryapp.catchabite.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliveryapp.catchabite.dto.DeliveryApiResponseDTO;
import com.deliveryapp.catchabite.entity.OrderDelivery;
import com.deliveryapp.catchabite.repository.OrderDeliveryRepository;
import com.deliveryapp.catchabite.security.AuthUser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/api/v1/rider")
@RequiredArgsConstructor
public class DeliveryLocationController {

    private final OrderDeliveryRepository orderDeliveryRepository;

    /**
     * 배달(내 배달) 좌표 조회: 매장/고객 좌표만 제공
     * GET /api/v1/rider/deliveries/{deliveryId}/coordinates
     */
    @GetMapping("/deliveries/{deliveryId}/coordinates")
    public ResponseEntity<DeliveryApiResponseDTO<DeliveryCoordinatesResponseDTO>> getDeliveryCoordinates(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal AuthUser user
    ) {
        // ✅ "내 배달" 검증 (배달원 principal 기반)
        OrderDelivery od = orderDeliveryRepository
                .findDeliveryForDeliverer(deliveryId, user.getDelivererId())
                .orElseThrow(() -> new IllegalArgumentException("조회 권한이 없거나 배달이 없습니다. deliveryId=" + deliveryId));

        // ✅ 좌표가 없으면 길찾기 불가 → 명확히 에러
        if (od.getStoreLatitude() == null || od.getStoreLongitude() == null) {
            throw new IllegalStateException("매장 좌표가 없습니다. storeLatitude/storeLongitude");
        }
        if (od.getDropoffLatitude() == null || od.getDropoffLongitude() == null) {
            throw new IllegalStateException("고객 좌표가 없습니다. dropoffLatitude/dropoffLongitude");
        }

        DeliveryCoordinatesResponseDTO data = new DeliveryCoordinatesResponseDTO();
        data.setDeliveryId(od.getDeliveryId());

        data.setStoreLatitude(od.getStoreLatitude());
        data.setStoreLongitude(od.getStoreLongitude());

        data.setDropoffLatitude(od.getDropoffLatitude());
        data.setDropoffLongitude(od.getDropoffLongitude());

        return ResponseEntity.ok(DeliveryApiResponseDTO.success("좌표 조회 성공", data));
    }

    // ---- Response DTO (원하면 별도 파일로 분리 추천) ----
    @Getter @Setter
    public static class DeliveryCoordinatesResponseDTO {
        private Long deliveryId;

        private BigDecimal storeLatitude;
        private BigDecimal storeLongitude;

        private BigDecimal dropoffLatitude;
        private BigDecimal dropoffLongitude;
    }
}