package com.deliveryapp.catchabite.auth.api.dto;

/**
 * 중복 여부 확인 API 응답 DTO
 */
public record ExistsResponse(
    boolean exists // 존재 여부 (true: 중복, false: 사용 가능)
) {}
