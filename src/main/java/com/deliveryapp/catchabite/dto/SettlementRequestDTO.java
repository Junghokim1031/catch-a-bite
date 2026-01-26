package com.deliveryapp.catchabite.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementRequestDTO {

    // 기간 정산
    private LocalDate from;
    private LocalDate to;

    // 선택 정산
    @Size(max = 200)
    private List<Long> deliveryIds;
}
