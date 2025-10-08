package com.quanvm.applyin.dto;

import java.time.Instant;
import com.quanvm.applyin.util.constant.UserEnum;
import java.util.List;

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
      UserEnum.Gender gender,
      List<String> skills, 
      List<Experience> experiences,
      List<Education> education,
      Boolean jobSeeking,
      Integer cvUploadCount,
      String coverLetter,
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
      String cvUrl,
      UserEnum.Gender gender,
      List<String> skills,
      List<Experience> experiences,
      List<Education> education,
      Boolean jobSeeking,
      Integer cvUploadCount,
      String coverLetter
  ) {}

  public record UpdateUserProfileWithFilesRequest(
      String fullName,
      String phoneNumber,
      String bio,
      Instant dateOfBirth,
      String address,
      String linkedinUrl,
      String githubUrl,
      UserEnum.Gender gender,
      List<String> skills,
      List<Experience> experiences,
      List<Education> education,
      Boolean jobSeeking,
      Integer cvUploadCount,
      String coverLetter
  ) {}

  public record Experience(String title, String company, String timeRange) {}
  public record Education(String degree, String school, String timeRange) {}
}


