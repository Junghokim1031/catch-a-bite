package com.deliveryapp.catchabite.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.deliveryapp.catchabite.common.response.ApiResponse;

/**
 * ✅ API(/api/**) 요청에서만 JSON 에러 응답을 만든다.
 * ✅ 페이지(Thymeleaf) 요청은 예외를 다시 던져서 Spring MVC 기본 에러 처리로 넘긴다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) throws Exception {

        String uri = request.getRequestURI();

        // ✅ /api/** 가 아니면 "처리하지 않는다" → 페이지는 HTML 렌더링/에러페이지로 간다.
        if (uri == null || !uri.startsWith("/api/")) {
            throw e;
        }

        // ✅ /api/** 인 경우에만 JSON으로 통일
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail("INTERNAL_ERROR",
                (e.getMessage() == null || e.getMessage().isBlank()) ? "Internal error" : e.getMessage()));
    }
}