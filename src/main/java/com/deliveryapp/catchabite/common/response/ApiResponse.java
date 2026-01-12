package com.deliveryapp.catchabite.common.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {

	private boolean success;
	private String code;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	private ApiResponse(boolean success, String code, String message, T data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, "OK", "success", data);
	}

	public static <T> ApiResponse<T> okMessage(String message) {
		return new ApiResponse<>(true, "OK", message, null);
	}

	public static <T> ApiResponse<T> fail(String code, String message) {
		return new ApiResponse<>(false, code, message, null);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
