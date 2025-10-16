package com.quanvm.applyin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class RecruiterDtos {

  public record RecruiterProfileRequest(
      @NotBlank String companyName,
      String companyWebsite,
      String companyAddress,
      String companySize,
      @Size(max = 5000) String about,
      String logoUrl,
      // recruiter personal profile
      String recruiterName,
      String recruiterTitle,
      String recruiterPhone,
      String recruiterEmail,
      String recruiterLinkedin,
      @Size(max = 5000) String recruiterAbout,
      String recruiterAvatarUrl
  ) {}

  public record RecruiterProfileResponse(
      Long id,
      String companyName,
      String companyWebsite,
      String companyAddress,
      String companySize,
      String about,
      String logoUrl,
      // recruiter personal profile
      String recruiterName,
      String recruiterTitle,
      String recruiterPhone,
      String recruiterEmail,
      String recruiterLinkedin,
      String recruiterAbout,
      String recruiterAvatarUrl,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record JobPostingRequest(
      @NotBlank String title,
      String location,
      String employmentType,
      List<String> description,
      List<String> requirements,
      List<String> benefits,
      Long salaryMin,
      Long salaryMax,
      Boolean active
  ) {}

  public record JobPostingResponse(
      Long id,
      String title,
      String location,
      String employmentType,
      List<String> description,
      List<String> requirements,
      List<String> benefits,
      Long salaryMin,
      Long salaryMax,
      boolean active,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record JobApplicationResponse(
      Long id,
      Long jobPostingId,
      Long candidateUserId,
      String cvUrl,
      String status,
      String note,
      Instant createdAt,
      Instant updatedAt
  ) {}
}


