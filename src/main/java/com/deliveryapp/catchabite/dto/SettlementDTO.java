package com.deliveryapp.catchabite.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.SettlementStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDTO {
    private Long settlementId;
    private LocalDate periodTo;
    private Long totalAmount;
    private SettlementStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime paidAt;
}
