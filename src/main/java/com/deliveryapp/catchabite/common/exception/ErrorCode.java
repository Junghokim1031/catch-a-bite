package com.deliveryapp.catchabite.common.exception;

/**
 * 공통 에러 코드 Enum
 * 비즈니스 예외 구분용
 */
public enum ErrorCode {

    INVALID_REQUEST("INVALID_REQUEST", "invalid request"),
    DUPLICATE_LOGIN_ID("DUPLICATE_LOGIN_ID", "duplicate login id"),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "duplicate nickname"),
    DUPLICATE_MOBILE("DUPLICATE_MOBILE", "duplicate mobile"),
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "account not found"),
    ACCOUNT_SUSPENDED("ACCOUNT_SUSPENDED", "account suspended"),
    ACCOUNT_WITHDRAWN("ACCOUNT_WITHDRAWN", "account withdrawn"),
    FORBIDDEN("FORBIDDEN", "forbidden");

    // 에러 코드 값
    private final String code;

    // 에러 메시지
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // 코드 반환
    public String getCode() {
        return code;
    }

    // 메시지 반환
    public String getMessage() {
        return message;
    }
}
