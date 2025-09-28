package com.quanvm.applyin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
  public record RegisterRequest(
      @NotBlank String fullName,
      @Email @NotBlank String email,
      @Size(min = 6) String password,
      String role // optional: only ADMIN can set non-default
  ) {}

  public record LoginRequest(
      @Email @NotBlank String email,
      @NotBlank String password,
      String deviceId,
      String deviceToken,
      String platform,
      String deviceName,
      String osVersion,
      String appVersion
  ) {}

  public record JwtResponse(
      String accessToken,
      Long id,
      String fullName,
      String email,
      String role,
      DeviceInfo deviceInfo
  ) {}

  public record DeviceInfo(
      Long deviceId,
      String deviceIdString,
      String deviceName,
      String platform,
      String osVersion,
      String appVersion,
      boolean isActive
  ) {}

  public record ForgotPasswordRequest(@Email @NotBlank String email) {}

  public record VerifyOtpRequest(
      @Email @NotBlank String email,
      @NotBlank @Size(min = 6, max = 6) String otpCode
  ) {}

  public record ResetPasswordRequest(
      @Email @NotBlank String email,
      @NotBlank @Size(min = 6) String newPassword
  ) {}

}


