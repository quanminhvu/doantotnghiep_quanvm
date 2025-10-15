package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.RecruiterDtos.*;
import com.quanvm.applyin.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

  private final RecruiterService recruiterService;

  private String email(Authentication auth) {
    return auth.getName();
  }

  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> getMyProfile(Authentication auth) {
    return ResponseEntity.ok(recruiterService.getMyProfile(email(auth)));
  }

  @PutMapping("/profile")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> upsertMyProfile(
      Authentication auth,
      @Valid @RequestBody RecruiterProfileRequest request
  ) {
    return ResponseEntity.ok(recruiterService.upsertMyProfile(email(auth), request));
  }

  @GetMapping("/jobs")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<List<JobPostingResponse>> listMyJobs(Authentication auth) {
    return ResponseEntity.ok(recruiterService.listMyJobs(email(auth)));
  }

  @PostMapping("/jobs")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<JobPostingResponse> createJob(
      Authentication auth,
      @Valid @RequestBody JobPostingRequest request
  ) {
    return ResponseEntity.ok(recruiterService.createJob(email(auth), request));
  }

  @PutMapping("/jobs/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<JobPostingResponse> updateJob(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody JobPostingRequest request
  ) {
    return ResponseEntity.ok(recruiterService.updateJob(email(auth), id, request));
  }

  @DeleteMapping("/jobs/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<Void> deleteJob(Authentication auth, @PathVariable Long id) {
    recruiterService.deleteJob(email(auth), id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/applications")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<List<JobApplicationResponse>> listApplications(Authentication auth) {
    return ResponseEntity.ok(recruiterService.listApplicationsForMyCompany(email(auth)));
  }

  @PutMapping("/applications/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<JobApplicationResponse> updateApplication(
      Authentication auth,
      @PathVariable Long id,
      @RequestParam String status,
      @RequestParam(required = false) String note
  ) {
    return ResponseEntity.ok(recruiterService.updateApplicationStatus(email(auth), id, status, note));
  }

  // Upload integration: client should first call /upload to get R2 URL, then set here
  @PostMapping("/profile/logo")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> setCompanyLogo(Authentication auth, @RequestParam String url) {
    return ResponseEntity.ok(recruiterService.updateLogoUrl(email(auth), url));
  }

  @PostMapping("/profile/avatar")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> setRecruiterAvatar(Authentication auth, @RequestParam String url) {
    return ResponseEntity.ok(recruiterService.updateAvatarUrl(email(auth), url));
  }
}


