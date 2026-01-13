package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOptionDTO {

    private Long orderOptionId;
    
    @NotNull
    private Long orderItemId;

    // null 뿐만 아니라 빈 문자열/공백도 막기
    @NotBlank
    @Size(max = 100)
    private String orderOptionName;

    // 기본값을 0으로 설정, 컬럼이 0또는 0이상의 값만 가지도록 설정
    // 후일 소수접 가격으로 확장시키기위새 사용.
    @Builder.Default
    @PositiveOrZero
    private Long orderOptionExtraPrice = 0L;
    
}
