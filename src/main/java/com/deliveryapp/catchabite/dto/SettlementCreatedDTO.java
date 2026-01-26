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
public class SettlementCreatedDTO {
    private Long settlementId;
    // 총 정산 금액
    private Long totalAmount;
    // 정산 요청 시간
    private LocalDateTime requestedAt;
}
