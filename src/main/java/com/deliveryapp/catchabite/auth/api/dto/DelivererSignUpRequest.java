package com.deliveryapp.catchabite.auth.api.dto;

import com.deliveryapp.catchabite.common.constant.PasswordPolicyConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 라이더 회원가입 요청 DTO */
public record DelivererSignUpRequest(
    @NotBlank
    String email, // 로그인 이메일

    @NotBlank
    @Pattern(
        regexp = PasswordPolicyConstant.PASSWORD_REGEX,
        message = PasswordPolicyConstant.PASSWORD_MESSAGE
    )
    String password, // 비밀번호

    @NotBlank
    String confirmPassword, // 비밀번호 확인

    @NotBlank
    String name, // 라이더 이름

    @NotBlank
    String mobile, // 휴대폰 번호

    @NotBlank
    String vehicleType, // WALKING/BICYCLE/MOTORBIKE/CAR

    String licenseNumber, // 오토바이/자동차만 필수
    String vehicleNumber // 오토바이/자동차만 필수
) {}
