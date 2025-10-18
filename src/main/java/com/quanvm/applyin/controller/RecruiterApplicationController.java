package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.RecruiterApplicationDtos.*;
import com.quanvm.applyin.service.RecruiterApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/applications")
@RequiredArgsConstructor
public class RecruiterApplicationController {

  private final RecruiterApplicationService recruiterApplicationService;

  private String email(Authentication auth) {
    return auth.getName();
  }

  @GetMapping
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<List<RecruiterApplicationResponse>> getApplicationsForMyJobs(
      Authentication auth,
      @RequestParam(required = false) Long jobId
  ) {
    return ResponseEntity.ok(recruiterApplicationService.getApplicationsForMyJobs(email(auth), jobId));
  }

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<List<RecruiterApplicationResponse>> getApplicationsForJob(
      Authentication auth,
      @PathVariable Long jobId
  ) {
    return ResponseEntity.ok(recruiterApplicationService.getApplicationsForJob(email(auth), jobId));
  }

  @GetMapping("/{applicationId}")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<RecruiterApplicationResponse> getApplication(
      Authentication auth,
      @PathVariable Long applicationId
  ) {
    return ResponseEntity.ok(recruiterApplicationService.getApplication(email(auth), applicationId));
  }

  @PutMapping("/{applicationId}/status")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<RecruiterApplicationResponse> updateApplicationStatus(
      Authentication auth,
      @PathVariable Long applicationId,
      @RequestBody UpdateApplicationStatusRequest request
  ) {
    return ResponseEntity.ok(recruiterApplicationService.updateApplicationStatus(
        email(auth), applicationId, request));
  }
}
