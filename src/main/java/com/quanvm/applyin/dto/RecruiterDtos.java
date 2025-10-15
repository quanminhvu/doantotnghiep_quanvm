package com.quanvm.applyin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

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
      @Size(max = 10000) String description,
      String requirements,
      String benefits,
      Long salaryMin,
      Long salaryMax,
      Boolean active
  ) {}

  public record JobPostingResponse(
      Long id,
      String title,
      String location,
      String employmentType,
      String description,
      String requirements,
      String benefits,
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


