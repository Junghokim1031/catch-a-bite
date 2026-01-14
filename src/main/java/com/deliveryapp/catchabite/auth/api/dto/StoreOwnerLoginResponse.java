package com.deliveryapp.catchabite.auth.api.dto;

/**
 * StoreOwnerLoginResponse: 사장님 로그인 성공 응답 DTO
 */
public record StoreOwnerLoginResponse(
    Long storeOwnerId,
    String storeOwnerName,
    String roleName
) {}
