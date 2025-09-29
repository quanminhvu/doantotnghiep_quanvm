package com.quanvm.applyin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class UserDtos {
  public record UserProfileResponse(Long id, String fullName, String email, String role) {}

  public record UserProfileDetailResponse(
      Long id,
      String fullName,
      String email,
      String role,
      String avatarUrl,
      String cvUrl,
      String phoneNumber,
      String bio,
      Instant dateOfBirth,
      String address,
      String linkedinUrl,
      String githubUrl,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record UpdateUserProfileRequest(
      String fullName,
      String phoneNumber,
      String bio,
      Instant dateOfBirth,
      String address,
      String linkedinUrl,
      String githubUrl,
      String avatarUrl,
      String cvUrl
  ) {}

  public record UpdateUserProfileWithFilesRequest(
      String fullName,
      String phoneNumber,
      String bio,
      Instant dateOfBirth,
      String address,
      String linkedinUrl,
      String githubUrl
  ) {}
}


