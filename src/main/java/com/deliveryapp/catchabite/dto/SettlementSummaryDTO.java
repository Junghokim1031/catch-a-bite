package com.deliveryapp.catchabite.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementSummaryDTO {
    // 기간 선택 (끝)
    private LocalDate periodFrom;
    // 기간 시작
    private LocalDate periodTo;
    // 정산 가능 횟수
    private long settleableCount;
    // 정산 가능 
    private long settleableAmount;
}
