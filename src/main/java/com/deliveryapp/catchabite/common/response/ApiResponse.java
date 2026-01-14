package com.deliveryapp.catchabite.common.response;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 포맷
 * 성공/실패 응답 표준화
 */
public class ApiResponse<T> {

    private boolean success;        // 요청 성공 여부
    private String code;            // 응답 코드
    private String message;         // 응답 메시지
    private T data;                 // 실제 응답 데이터
    private LocalDateTime timestamp;// 응답 시간

    // 응답 객체 생성자
    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", "success", data);
    }

    // 성공 응답 (메시지만)
    public static <T> ApiResponse<T> okMessage(String message) {
        return new ApiResponse<>(true, "OK", message, null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    // 성공 여부 반환
    public boolean isSuccess() {
        return success;
    }

    // 코드 반환
    public String getCode() {
        return code;
    }

    // 메시지 반환
    public String getMessage() {
        return message;
    }

    // 데이터 반환
    public T getData() {
        return data;
    }

    // 응답 시간 반환
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
