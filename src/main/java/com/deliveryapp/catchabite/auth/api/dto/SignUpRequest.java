package com.deliveryapp.catchabite.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 */
public record SignUpRequest(

    @NotBlank
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 올바르지 않습니다."
    )
    String loginId, // 로그인 ID로 사용하는 이메일

    @NotBlank
    @Pattern(
        regexp = "^(01[016789])\\d{3,4}\\d{4}$",
        message = "휴대폰 번호 형식이 올바르지 않습니다.(예: 01012345678)"
    )
    String mobile, // 휴대폰 번호

    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{8,64}$",
        message = "비밀번호는 영문/숫자/특수문자 포함 8~64자리여야 합니다."
    )
    String password, // 로그인 비밀번호

    @NotBlank
    String confirmPassword, // 비밀번호 확인용

    @NotBlank
    @Size(min = 2, max = 20)
    String nickname, // 사용자 닉네임

    @NotBlank
    @Size(min = 2, max = 30)
    String name, // 사용자 실명

    boolean requiredTermsAccepted,   // 필수 약관 동의 여부
    boolean marketingTermsAccepted   // 마케팅 수신 약관 동의 여부(선택)
) {}
