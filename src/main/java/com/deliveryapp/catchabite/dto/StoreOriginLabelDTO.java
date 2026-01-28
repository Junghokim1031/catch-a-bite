package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 가게 원산지 표기(텍스트) 관리용 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreOriginLabelDTO {

    @Size(max = 4000)
    private String originLabel;
}
