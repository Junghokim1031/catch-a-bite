package com.deliveryapp.catchabite.auth.api.dto;

/**
 * 로그인 사용자 정보 응답 DTO
 */
public record MeResponse(
    Long userId,
    String loginKey,
    String name,
    String roleName,
    String mobile,
    String accountType
) {}
