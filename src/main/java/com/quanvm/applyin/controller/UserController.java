package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.ApiResponse;
import com.quanvm.applyin.dto.UserDtos.UserProfileResponse;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/me")
  @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','CANDIDATE')")
  public ResponseEntity<ApiResponse<UserProfileResponse>> me(@AuthenticationPrincipal UserDetails principal) {
    User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
    UserProfileResponse data = new UserProfileResponse(
        user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    return ResponseEntity.ok(ApiResponse.ok("Fetched profile", data));
  }
}


