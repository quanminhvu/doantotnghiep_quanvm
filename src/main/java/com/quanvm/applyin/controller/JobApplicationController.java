package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.JobApplicationDtos.*;
import com.quanvm.applyin.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

  private final JobApplicationService jobApplicationService;

  private String email(Authentication auth) {
    return auth.getName();
  }

  @PostMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<JobApplicationResponse> applyForJob(
      Authentication auth,
      @Valid @RequestBody JobApplicationRequest request
  ) {
    return ResponseEntity.ok(jobApplicationService.applyForJob(email(auth), request));
  }

  @GetMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<List<JobApplicationResponse>> getMyApplications(Authentication auth) {
    return ResponseEntity.ok(jobApplicationService.getMyApplications(email(auth)));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<JobApplicationResponse> getApplication(
      Authentication auth,
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(jobApplicationService.getApplication(email(auth), id));
  }

  @PutMapping("/{id}/withdraw")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<JobApplicationResponse> withdrawApplication(
      Authentication auth,
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(jobApplicationService.withdrawApplication(email(auth), id));
  }

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<List<JobApplicationResponse>> getApplicationsForJob(
      Authentication auth,
      @PathVariable Long jobId
  ) {
    return ResponseEntity.ok(jobApplicationService.getApplicationsForJob(email(auth), jobId));
  }

  @GetMapping("/job/{jobId}/count")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<Integer> getApplicationCountForJob(
      Authentication auth,
      @PathVariable Long jobId
  ) {
    return ResponseEntity.ok(jobApplicationService.getApplicationCountForJob(email(auth), jobId));
  }
}
