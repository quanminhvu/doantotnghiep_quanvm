package com.quanvm.applyin.dto;

import java.time.Instant;

public class RecruiterApplicationDtos {

  public record RecruiterApplicationResponse(
      Long id,
      Long jobId,
      String jobTitle,
      String candidateName,
      String candidateEmail,
      String candidatePhone,
      String cvUrl,
      String coverLetter,
      String note,
      String status,
      Integer applicationNumber,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record UpdateApplicationStatusRequest(
      String status
  ) {}

  public record ApplicationStatsResponse(
      Long totalApplications,
      Long pendingApplications,
      Long reviewedApplications,
      Long acceptedApplications,
      Long rejectedApplications
  ) {}
}
