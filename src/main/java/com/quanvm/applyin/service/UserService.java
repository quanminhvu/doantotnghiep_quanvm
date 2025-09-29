package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.UserDtos.UpdateUserProfileRequest;
import com.quanvm.applyin.dto.UserDtos.UserProfileDetailResponse;
import com.quanvm.applyin.entity.ProfileUser;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.exception.UserNotFoundException;
import com.quanvm.applyin.repository.ProfileUserRepository;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileUserRepository profileUserRepository;

    public UserProfileDetailResponse getUserProfile(String userEmail) {
        User user = getUserByEmail(userEmail);
        ProfileUser profile = profileUserRepository.findByUser(user).orElse(null);

        return new UserProfileDetailResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                profile != null ? profile.getAvatarUrl() : null,
                profile != null ? profile.getCvUrl() : null,
                profile != null ? profile.getPhoneNumber() : null,
                profile != null ? profile.getBio() : null,
                profile != null ? profile.getDateOfBirth() : null,
                profile != null ? profile.getAddress() : null,
                profile != null ? profile.getLinkedinUrl() : null,
                profile != null ? profile.getGithubUrl() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public UserProfileDetailResponse updateUserProfile(String userEmail, UpdateUserProfileRequest request) {
        User user = getUserByEmail(userEmail);
        
        // Cập nhật thông tin user cơ bản
        if (request.fullName() != null && !request.fullName().trim().isEmpty()) {
            user.setFullName(request.fullName().trim());
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }

        // Tìm hoặc tạo profile
        ProfileUser profile = profileUserRepository.findByUser(user)
                .orElse(ProfileUser.builder()
                        .user(user)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());

        // Cập nhật thông tin profile
        updateProfileFields(profile, request);
        profile.setUpdatedAt(Instant.now());
        profileUserRepository.save(profile);

        return getUserProfile(userEmail);
    }

    private void updateProfileFields(ProfileUser profile, UpdateUserProfileRequest request) {
        if (request.phoneNumber() != null) {
            profile.setPhoneNumber(request.phoneNumber().trim().isEmpty() ? null : request.phoneNumber().trim());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio().trim().isEmpty() ? null : request.bio().trim());
        }
        if (request.dateOfBirth() != null) {
            profile.setDateOfBirth(request.dateOfBirth());
        }
        if (request.address() != null) {
            profile.setAddress(request.address().trim().isEmpty() ? null : request.address().trim());
        }
        if (request.linkedinUrl() != null) {
            profile.setLinkedinUrl(request.linkedinUrl().trim().isEmpty() ? null : request.linkedinUrl().trim());
        }
        if (request.githubUrl() != null) {
            profile.setGithubUrl(request.githubUrl().trim().isEmpty() ? null : request.githubUrl().trim());
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(request.avatarUrl().trim().isEmpty() ? null : request.avatarUrl().trim());
        }
        if (request.cvUrl() != null) {
            profile.setCvUrl(request.cvUrl().trim().isEmpty() ? null : request.cvUrl().trim());
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email không tồn tại trong hệ thống"));
    }
}
