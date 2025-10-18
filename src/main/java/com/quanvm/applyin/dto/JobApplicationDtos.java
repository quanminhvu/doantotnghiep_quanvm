package com.quanvm.applyin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

public class JobApplicationDtos {

  @Builder
  public record JobApplicationRequest(
      @NotNull(message = "Job ID is required")
      Long jobId,
      String cvUrl,
      String coverLetter,
      String note
  ) {}

  @Builder
  public record JobApplicationResponse(
      Long id,
      Long jobId,
      String jobTitle,
      String companyName,
      String cvUrl,
      String coverLetter,
      String note,
      String status,
      Instant createdAt,
      Instant updatedAt
  ) {}

  @Builder
  public record CvOption(
      Long id,
      String fileName,
      String url,
      String description,
      boolean isPrimary,
      Instant uploadedAt
  ) {}
}
