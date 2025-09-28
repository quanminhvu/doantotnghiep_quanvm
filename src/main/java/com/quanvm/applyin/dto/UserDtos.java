package com.quanvm.applyin.dto;

public class UserDtos {
  public record UserProfileResponse(Long id, String fullName, String email, String role) {}
}


