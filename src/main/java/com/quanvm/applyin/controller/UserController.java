package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.ApiResponse;
import com.quanvm.applyin.dto.UserDtos.UpdateUserProfileRequest;
import com.quanvm.applyin.dto.UserDtos.UpdateUserProfileWithFilesRequest;
import com.quanvm.applyin.dto.UserDtos.UserProfileDetailResponse;
import com.quanvm.applyin.dto.UserDtos.UserProfileResponse;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.service.UserService;
import com.quanvm.applyin.service.FileUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final FileUploadService fileUploadService;
  private final ObjectMapper objectMapper;

  @GetMapping("/me")
  @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','CANDIDATE')")
  public ResponseEntity<ApiResponse<UserProfileResponse>> me(@AuthenticationPrincipal UserDetails principal) {
    User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
    UserProfileResponse data = new UserProfileResponse(
        user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin cơ bản thành công", data));
  }

  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','CANDIDATE')")
  public ResponseEntity<ApiResponse<UserProfileDetailResponse>> getProfile(Authentication authentication) {
    String userEmail = authentication.getName();
    UserProfileDetailResponse profile = userService.getUserProfile(userEmail);
    return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin profile thành công", profile));
  }

  @PutMapping("/profile")
  @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','CANDIDATE')")
  public ResponseEntity<ApiResponse<UserProfileDetailResponse>> updateProfile(
      @Valid @RequestBody UpdateUserProfileRequest request,
      Authentication authentication) {
    String userEmail = authentication.getName();
    UserProfileDetailResponse updatedProfile = userService.updateUserProfile(userEmail, request);
    return ResponseEntity.ok(ApiResponse.ok("Cập nhật profile thành công", updatedProfile));
  }

  @PutMapping("/profile/with-files")
  @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','CANDIDATE')")
  public ResponseEntity<ApiResponse<UserProfileDetailResponse>> updateProfileWithFiles(
      @RequestParam(value = "avatar", required = false) MultipartFile avatar,
      @RequestParam(value = "cv", required = false) MultipartFile cv,
      @RequestParam("profileData") String profileDataJson,
      Authentication authentication) throws Exception {
    String userEmail = authentication.getName();
    
    // Parse JSON data
    UpdateUserProfileWithFilesRequest profileData = objectMapper.readValue(
        profileDataJson, UpdateUserProfileWithFilesRequest.class);

    // Upload files and get URLs
    String avatarUrl = null;
    String cvUrl = null;
    
    if (avatar != null && !avatar.isEmpty()) {
      avatarUrl = fileUploadService.uploadFile(avatar);
    }
    
    if (cv != null && !cv.isEmpty()) {
      cvUrl = fileUploadService.uploadFile(cv);
    }

    // Create update request with file URLs
    UpdateUserProfileRequest request = new UpdateUserProfileRequest(
        profileData.fullName(),
        profileData.phoneNumber(),
        profileData.bio(),
        profileData.dateOfBirth(),
        profileData.address(),
        profileData.linkedinUrl(),
        profileData.githubUrl(),
        avatarUrl,
        cvUrl
    );

    UserProfileDetailResponse updatedProfile = userService.updateUserProfile(userEmail, request);
    return ResponseEntity.ok(ApiResponse.ok("Cập nhật profile với files thành công", updatedProfile));
  }
}


