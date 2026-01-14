package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * StoreOwnerLoginRequest: 사장님 로그인 요청 DTO
 */
public record StoreOwnerLoginRequest(

    @NotBlank
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 올바르지 않습니다."
    )
    String email,

    @NotBlank
    @Size(min = 8, max = 30)
    String password
) {}
