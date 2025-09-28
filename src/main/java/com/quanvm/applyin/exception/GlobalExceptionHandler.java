package com.quanvm.applyin.exception;

import com.quanvm.applyin.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        log.error("EmailAlreadyExistsException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("UserNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("InvalidTokenException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpExpiredException(OtpExpiredException ex) {
        log.error("OtpExpiredException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(OtpNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpNotVerifiedException(OtpNotVerifiedException ex) {
        log.error("OtpNotVerifiedException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("BadCredentialsException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.of(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                        "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.", null));
    }
}
