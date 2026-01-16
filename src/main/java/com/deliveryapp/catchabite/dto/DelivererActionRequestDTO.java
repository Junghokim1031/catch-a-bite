package com.deliveryapp.catchabite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// 'DeliveryController'에서 사용
// '배달원의 행동 관련 - 배달원 관점'
// 나중에 Spring Security 붙이면 delivererId는 DTO에서 빼고 로그인 정보에서 꺼내는 게 정석.
@Getter @Setter
public class DelivererActionRequestDTO {

    @NotNull @Positive
    private Long delivererId;

}
