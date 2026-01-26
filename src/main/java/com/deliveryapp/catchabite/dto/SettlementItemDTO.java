package com.deliveryapp.catchabite.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementItemDTO { // 배달 품목 DTO
    private Long deliveryId;
    // 거리(meter 기준)
    private Long distanceM;
    // 기본 적용 금액
    private Long appliedBaseFee;
    // 추가 적용 금액
    private Long appliedPerKmFee;
    // 총 수익
    private Long earningAmount;
    // 배달 완료 시간
    private LocalDateTime deliveredAt;
}
