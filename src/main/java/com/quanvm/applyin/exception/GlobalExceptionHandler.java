package com.quanvm.applyin.exception;

import com.quanvm.applyin.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
    log.warn("EmailAlreadyExistsException: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException ex) {
    log.warn("UserNotFoundException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
    log.warn("InvalidTokenException: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
  }

  @ExceptionHandler({OtpExpiredException.class, OtpNotVerifiedException.class})
  public ResponseEntity<ApiResponse<Object>> handleOtpExceptions(RuntimeException ex) {
    log.warn("OTP Exception: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("BadCredentialsException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    log.warn("Validation error: {}", errors);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ", errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
    log.warn("ConstraintViolationException: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("AccessDeniedException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.of(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền truy cập tài nguyên này", null));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    log.warn("MaxUploadSizeExceededException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(ApiResponse.of(HttpStatus.PAYLOAD_TOO_LARGE.value(), "File upload quá lớn. Vui lòng chọn file nhỏ hơn.", null));
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ApiResponse<Object>> handleIOException(IOException ex) {
    log.error("IOException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi xử lý file. Vui lòng thử lại.", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.", null));
  }
}

