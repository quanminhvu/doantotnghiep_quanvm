package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.UserDtos.UpdateUserProfileRequest;
import com.quanvm.applyin.dto.UserDtos.UserProfileDetailResponse;
import com.quanvm.applyin.dto.UserDtos.Experience;
import com.quanvm.applyin.dto.UserDtos.Education;
import com.quanvm.applyin.entity.ProfileUser;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.exception.UserNotFoundException;
import com.quanvm.applyin.repository.ProfileUserRepository;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileUserRepository profileUserRepository;
    private final ObjectMapper objectMapper;

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
                profile != null ? profile.getGender() : null,
                profile != null ? parseSkills(profile.getSkillsJson()) : Collections.emptyList(),
                profile != null ? parseExperiences(profile.getExperiencesJson()) : Collections.emptyList(),
                profile != null ? parseEducation(profile.getEducationJson()) : Collections.emptyList(),
                profile != null ? profile.getJobSeeking() : null,
                profile != null ? profile.getCvUploadCount() : null,
                profile != null ? profile.getCoverLetter() : null,
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
        if (request.gender() != null) {
            profile.setGender(request.gender());
        }
        if (request.skills() != null) {
            profile.setSkillsJson(writeJson(request.skills()));
        }
        if (request.experiences() != null) {
            profile.setExperiencesJson(writeJson(request.experiences()));
        }
        if (request.education() != null) {
            profile.setEducationJson(writeJson(request.education()));
        }
        if (request.jobSeeking() != null) {
            profile.setJobSeeking(request.jobSeeking());
        }
        if (request.cvUploadCount() != null) {
            profile.setCvUploadCount(request.cvUploadCount());
        }
        if (request.coverLetter() != null) {
            profile.setCoverLetter(request.coverLetter().trim().isEmpty() ? null : request.coverLetter().trim());
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email không tồn tại trong hệ thống"));
    }

    private List<String> parseSkills(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Experience> parseExperiences(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Experience>>(){});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Education> parseEducation(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Education>>(){});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public UserProfileDetailResponse updateAvatarUrl(String userEmail, String avatarUrl) {
        User user = getUserByEmail(userEmail);
        ProfileUser profile = profileUserRepository.findByUser(user)
                .orElse(ProfileUser.builder()
                        .user(user)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
        profile.setAvatarUrl(avatarUrl);
        profile.setUpdatedAt(Instant.now());
        profileUserRepository.save(profile);
        return getUserProfile(userEmail);
    }

    @Transactional
    public UserProfileDetailResponse updateCvUrl(String userEmail, String cvUrl) {
        User user = getUserByEmail(userEmail);
        ProfileUser profile = profileUserRepository.findByUser(user)
                .orElse(ProfileUser.builder()
                        .user(user)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
        profile.setCvUrl(cvUrl);
        Integer count = profile.getCvUploadCount() == null ? 0 : profile.getCvUploadCount();
        profile.setCvUploadCount(count + 1);
        profile.setUpdatedAt(Instant.now());
        profileUserRepository.save(profile);
        return getUserProfile(userEmail);
    }
}
