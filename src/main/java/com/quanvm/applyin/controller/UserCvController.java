package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.CvDtos;
import com.quanvm.applyin.dto.ApiResponse;
import com.quanvm.applyin.service.UserCvService;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/cvs")
@RequiredArgsConstructor
@Slf4j
public class UserCvController {

  private final UserCvService userCvService;
  private final UserRepository userRepository;

  @PostMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> uploadCv(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
      Authentication authentication) {
    
    try {
      log.debug("[CV][UPLOAD] fileName={}, description={}, isPrimary={}", file.getOriginalFilename(), description, isPrimary);
      
      CvDtos.CvUploadRequest request = CvDtos.CvUploadRequest.builder()
          .description(description)
          .isPrimary(isPrimary)
          .build();
      
      Long userId = getCurrentUserId(authentication);
      
      CvDtos.CvResponse response = userCvService.uploadCv(userId, file, request);
      log.debug("[CV][UPLOAD] success userId={}, cvId={}", userId, response.getId());
      
      return ResponseEntity.ok().body(ApiResponse.ok("CV uploaded successfully", response));
    } catch (Exception e) {
      log.error("[CV][UPLOAD] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to upload CV: " + e.getMessage()));
    }
  }

  @GetMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getUserCvs(Authentication authentication) {
    try {
      log.debug("[CV][LIST] start");
      
      Long userId = getCurrentUserId(authentication);
      CvDtos.CvListResponse response = userCvService.getUserCvs(userId);
      
      log.debug("[CV][LIST] success userId={}, total={}", userId, response.getTotalCount());
      return ResponseEntity.ok().body(ApiResponse.ok("CVs retrieved successfully", response));
    } catch (Exception e) {
      log.error("[CV][LIST] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to get CVs: " + e.getMessage()));
    }
  }

  @GetMapping("/{cvId}")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getCvById(@PathVariable Long cvId, Authentication authentication) {
    try {
      log.debug("[CV][GET] cvId={}", cvId);
      
      Long userId = getCurrentUserId(authentication);
      CvDtos.CvResponse response = userCvService.getCvById(userId, cvId);
      
      log.debug("[CV][GET] success userId={}, cvId={}", userId, response.getId());
      return ResponseEntity.ok().body(ApiResponse.ok("CV retrieved successfully", response));
    } catch (Exception e) {
      log.error("[CV][GET] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to get CV: " + e.getMessage()));
    }
  }

  @PutMapping("/{cvId}")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateCv(
      @PathVariable Long cvId,
      @RequestBody CvDtos.CvUpdateRequest request,
      Authentication authentication) {
    try {
      log.debug("[CV][UPDATE] cvId={}, request={}", cvId, request);
      
      Long userId = getCurrentUserId(authentication);
      CvDtos.CvResponse response = userCvService.updateCv(userId, cvId, request);
      
      log.debug("[CV][UPDATE] success userId={}, cvId={}", userId, response.getId());
      return ResponseEntity.ok().body(ApiResponse.ok("CV updated successfully", response));
    } catch (Exception e) {
      log.error("[CV][UPDATE] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to update CV: " + e.getMessage()));
    }
  }

  @DeleteMapping("/{cvId}")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> deleteCv(@PathVariable Long cvId, Authentication authentication) {
    try {
      log.debug("[CV][DELETE] cvId={}", cvId);
      
      Long userId = getCurrentUserId(authentication);
      userCvService.deleteCv(userId, cvId);
      
      log.debug("[CV][DELETE] success userId={}, cvId={}", userId, cvId);
      return ResponseEntity.ok().body(ApiResponse.ok("CV deleted successfully", null));
    } catch (Exception e) {
      log.error("[CV][DELETE] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to delete CV: " + e.getMessage()));
    }
  }

  @PutMapping("/{cvId}/set-primary")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> setPrimaryCv(@PathVariable Long cvId, Authentication authentication) {
    try {
      log.debug("[CV][SET_PRIMARY] cvId={}", cvId);
      
      Long userId = getCurrentUserId(authentication);
      CvDtos.CvResponse response = userCvService.setPrimaryCv(userId, cvId);
      
      log.debug("[CV][SET_PRIMARY] success userId={}, cvId={}", userId, response.getId());
      return ResponseEntity.ok().body(ApiResponse.ok("Primary CV set successfully", response));
    } catch (Exception e) {
      log.error("[CV][SET_PRIMARY] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to set primary CV: " + e.getMessage()));
    }
  }

  @GetMapping("/primary")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getPrimaryCv(Authentication authentication) {
    try {
      log.debug("[CV][GET_PRIMARY] start");
      
      Long userId = getCurrentUserId(authentication);
      var primaryCv = userCvService.getPrimaryCv(userId);
      
      if (primaryCv.isPresent()) {
        log.debug("[CV][GET_PRIMARY] success userId={}, cvId={}", userId, primaryCv.get().getId());
        return ResponseEntity.ok().body(ApiResponse.ok("Primary CV retrieved successfully", primaryCv.get()));
      } else {
        log.debug("[CV][GET_PRIMARY] none userId={}", userId);
        return ResponseEntity.ok().body(ApiResponse.ok("No primary CV found", null));
      }
    } catch (Exception e) {
      log.error("[CV][GET_PRIMARY] error: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to get primary CV: " + e.getMessage()));
    }
  }

  private Long getCurrentUserId(Authentication authentication) {
    String userEmail = authentication.getName();
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return user.getId();
  }

}
