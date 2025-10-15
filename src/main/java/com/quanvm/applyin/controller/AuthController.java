package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.ApiResponse;
import com.quanvm.applyin.dto.AuthDtos.ChangePasswordRequest;
import com.quanvm.applyin.dto.AuthDtos.ForgotPasswordRequest;
import com.quanvm.applyin.dto.AuthDtos.JwtResponse;
import com.quanvm.applyin.dto.AuthDtos.LoginRequest;
import com.quanvm.applyin.dto.AuthDtos.LogoutRequest;
import com.quanvm.applyin.dto.AuthDtos.RegisterRequest;
import com.quanvm.applyin.dto.AuthDtos.ResetPasswordRequest;
import com.quanvm.applyin.dto.AuthDtos.VerifyOtpRequest;
import com.quanvm.applyin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.debug("[AUTH][REGISTER] Incoming register: fullName={}, email={}, role={}", request.fullName(), request.email(), request.role());
            authService.register(request);
            log.debug("[AUTH][REGISTER] Register success: email={}", request.email());
            return ResponseEntity.ok(ApiResponse.ok("Đăng ký thành công", null));
        } catch (Exception e) {
            log.error("[AUTH][REGISTER] Register failed: email={}, error={}", request.email(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.debug("[AUTH][LOGIN] Incoming login: email={}", request.email());
            JwtResponse data = authService.login(request);
            log.debug("[AUTH][LOGIN] Login success: email={}, userId={}", data.email(), data.id());
            return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", data));
        } catch (Exception e) {
            log.error("[AUTH][LOGIN] Login failed: email={}, error={}", request.email(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.ok("Mã reset đã được gửi đến email của bạn", null));
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            authService.verifyOtp(request);
            return ResponseEntity.ok(ApiResponse.ok("Xác thực OTP thành công", null));
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.ok("Mật khẩu đã được đặt lại thành công", null));
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            authService.logout(request, userEmail);
            return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công", null));
        } catch (Exception e) {
            throw e;
        }
    }

     @PostMapping("/logout-all")
     public ResponseEntity<ApiResponse<Void>> logoutAll(Authentication authentication) {
         try {
             String userEmail = authentication.getName();
             authService.logoutAllDevices(userEmail);
             return ResponseEntity.ok(ApiResponse.ok("Đăng xuất tất cả thiết bị thành công", null));
         } catch (Exception e) {
             throw e;
         }
     }

     @PostMapping("/change-password")
     public ResponseEntity<ApiResponse<Void>> changePassword(
             @Valid @RequestBody ChangePasswordRequest request,
             Authentication authentication) {
         try {
             String userEmail = authentication.getName();
             authService.changePassword(request, userEmail);
             return ResponseEntity.ok(ApiResponse.ok("Đổi mật khẩu thành công", null));
         } catch (Exception e) {
             throw e;
         }
     }
 }


