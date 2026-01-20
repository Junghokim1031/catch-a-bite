package com.deliveryapp.catchabite.common.exception;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API(/api/**) exceptions return JSON responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
        InvalidCredentialsException e,
        HttpServletRequest request
    ) throws Exception {
        if (!isApiRequest(request)) {
            throw e;
        }

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.fail("INVALID_CREDENTIALS", resolveMessage(e, "Invalid credentials.")));
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        MethodArgumentNotValidException.class,
        BindException.class,
        ConstraintViolationException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e, HttpServletRequest request) throws Exception {
        if (!isApiRequest(request)) {
            throw e;
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("BAD_REQUEST", resolveMessage(e, "Bad request")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) throws Exception {
        if (!isApiRequest(request)) {
            throw e;
        }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail("INTERNAL_ERROR", resolveMessage(e, "Internal error")));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
        IllegalStateException e,
        HttpServletRequest request
    ) throws Exception {
        if (!isApiRequest(request)) {
            throw e;
        }

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.fail("UNAUTHORIZED", resolveMessage(e, "Unauthorized")));
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/");
    }

    private String resolveMessage(Exception e, String fallback) {
        String validationMessage = resolveValidationMessage(e);
        if (validationMessage != null && !validationMessage.isBlank()) {
            return validationMessage;
        }

        String message = e.getMessage();
        return (message == null || message.isBlank()) ? fallback : message;
    }

    private String resolveValidationMessage(Exception e) {
        if (e instanceof MethodArgumentNotValidException ex) {
            return extractBindingMessage(ex.getBindingResult());
        }
        if (e instanceof BindException ex) {
            return extractBindingMessage(ex.getBindingResult());
        }
        if (e instanceof ConstraintViolationException ex) {
            return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    private String extractBindingMessage(BindingResult bindingResult) {
        if (bindingResult == null) {
            return null;
        }
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null
            && !fieldError.getDefaultMessage().isBlank()) {
            return fieldError.getDefaultMessage();
        }
        ObjectError objectError = bindingResult.getGlobalError();
        if (objectError != null && objectError.getDefaultMessage() != null
            && !objectError.getDefaultMessage().isBlank()) {
            return objectError.getDefaultMessage();
        }
        return null;
    }
}
