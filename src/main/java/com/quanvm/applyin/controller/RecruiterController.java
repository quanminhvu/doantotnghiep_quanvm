package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.RecruiterDtos.*;
import com.quanvm.applyin.service.RecruiterService;
import com.quanvm.applyin.service.FileUploadService;
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
  private final FileUploadService fileUploadService;

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

  @PutMapping("/profile/logo")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> setCompanyLogo(Authentication auth, @RequestParam String url) {
    return ResponseEntity.ok(recruiterService.updateLogoUrl(email(auth), url));
  }

  @PutMapping("/profile/avatar")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> setRecruiterAvatar(Authentication auth, @RequestParam String url) {
    return ResponseEntity.ok(recruiterService.updateAvatarUrl(email(auth), url));
  }

  @PutMapping("/profile/logo/upload")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> uploadCompanyLogo(
      Authentication auth,
      @RequestParam("logo") org.springframework.web.multipart.MultipartFile logo
  ) throws Exception {
    if (logo == null || logo.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    String url = fileUploadService.uploadFile(logo, "recruiter/logo");
    return ResponseEntity.ok(recruiterService.updateLogoUrl(email(auth), url));
  }

  @PutMapping("/profile/avatar/upload")
  @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
  public ResponseEntity<RecruiterProfileResponse> uploadRecruiterAvatar(
      Authentication auth,
      @RequestParam("avatar") org.springframework.web.multipart.MultipartFile avatar
  ) throws Exception {
    if (avatar == null || avatar.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    String url = fileUploadService.uploadFile(avatar, "recruiter/avatar");
    return ResponseEntity.ok(recruiterService.updateAvatarUrl(email(auth), url));
  }
}


