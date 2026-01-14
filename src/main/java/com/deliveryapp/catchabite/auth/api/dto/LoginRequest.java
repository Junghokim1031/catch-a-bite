package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 */
public record LoginRequest(

    @NotBlank
    String loginKey, // 로그인 식별값 (이메일 또는 휴대폰 번호)

    @NotBlank
    String password // 로그인 비밀번호
) {}
