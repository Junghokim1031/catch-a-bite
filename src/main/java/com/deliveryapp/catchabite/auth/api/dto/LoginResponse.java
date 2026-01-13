package com.deliveryapp.catchabite.auth.api.dto;

/**
 * 로그인 성공 시 사용자 기본 정보 응답 DTO
 */
public record LoginResponse(
    Long accountId,  // 계정 고유 ID
    String nickname, // 사용자 닉네임
    String roleName  // 사용자 권한 역할
) {}
