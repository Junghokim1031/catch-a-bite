package com.deliveryapp.catchabite.common.exception;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AppException.class)
	public ApiResponse<Void> handleAppException(AppException e) {
		System.out.println("GlobalExceptionHandler - handleAppException");
		return ApiResponse.fail(e.getErrorCode().getCode(), e.getMessage());
	}

	@ExceptionHandler(BindException.class)
	public ApiResponse<Void> handleBindException(BindException e) {
		System.out.println("GlobalExceptionHandler - handleBindException");
		String message = (e.getBindingResult().getFieldError() != null)
			? e.getBindingResult().getFieldError().getDefaultMessage()
			: ErrorCode.INVALID_REQUEST.getMessage();
		return ApiResponse.fail(ErrorCode.INVALID_REQUEST.getCode(), message);
	}

	@ExceptionHandler(Exception.class)
	public ApiResponse<Void> handleException(Exception e) {
		System.out.println("GlobalExceptionHandler - handleException");
		return ApiResponse.fail("INTERNAL_ERROR", e.getMessage());
	}
}
