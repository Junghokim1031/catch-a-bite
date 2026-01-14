package com.deliveryapp.catchabite.auth.api.dto;

/**
 * 회원가입 완료 후 사용자 정보 응답 DTO
 */
public record SignUpResponse(
    Long accountId, // 계정 고유 ID
    String loginId, // 로그인 ID(이메일)
    String nickname, // 사용자 닉네임
    String roleName  // 사용자 권한 역할
) {}
