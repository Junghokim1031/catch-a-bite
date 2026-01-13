package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/** 라이더 로그인 요청 DTO */
public record DelivererLoginRequest(
    @NotBlank String email, // 로그인 이메일
    @NotBlank String password // 비밀번호
) {}
