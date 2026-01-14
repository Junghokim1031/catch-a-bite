package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * StoreOwnerSignUpRequest: 사장님 회원가입 요청 DTO
 */
public record StoreOwnerSignUpRequest(

    @NotBlank
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 올바르지 않습니다."
    )
    String email,

    @NotBlank
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자여야 합니다.")
    String password,

    @NotBlank
    String confirmPassword,

    @NotBlank
    @Size(min = 2, max = 100)
    String name,

    @NotBlank
    @Size(min = 10, max = 11, message = "휴대폰 번호는 숫자만 10~11자리로 입력하세요.")
    String mobile
) {}
