package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/** 라이더 회원가입 요청 DTO */
public record DelivererSignUpRequest(
    @NotBlank String email, // 로그인 이메일
    @NotBlank String password, // 비밀번호
    @NotBlank String vehicleType, // WALKING/BICYCLE/MOTORCYCLE/CAR
    String licenseNumber, // 오토바이/자동차만 필수
    String vehicleNumber // 오토바이/자동차만 필수
) {}
