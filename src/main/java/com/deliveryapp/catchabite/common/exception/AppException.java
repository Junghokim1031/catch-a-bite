package com.deliveryapp.catchabite.common.exception;

/**
 * 공통 비즈니스 예외 클래스
 * ErrorCode 기반 예외 처리용
 */
public class AppException extends RuntimeException {

    // 에러 코드 정보
    private final ErrorCode errorCode;

    // ErrorCode 메시지로 예외 생성
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 커스텀 메시지 포함 예외 생성
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // 에러 코드 반환
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
