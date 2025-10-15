package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.CvDtos;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.entity.UserCv;
import com.quanvm.applyin.repository.UserCvRepository;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCvService {

  private final UserCvRepository userCvRepository;
  private final UserRepository userRepository;
  private final FileUploadService fileUploadService;

  @Transactional
  public CvDtos.CvResponse uploadCv(Long userId, MultipartFile file, CvDtos.CvUploadRequest request) {
    log.info("Uploading CV for user: {}", userId);
    
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    // Upload file to storage
    String cvUrl;
    try {
      cvUrl = fileUploadService.uploadFile(file, "cvs");
    } catch (IOException e) {
      log.error("Failed to upload file: {}", e.getMessage());
      throw new RuntimeException("Failed to upload file: " + e.getMessage());
    }
    
    // If this is set as primary, unset other primary CVs
    if (Boolean.TRUE.equals(request.getIsPrimary())) {
      unsetPrimaryCvs(userId);
    }
    
    // Create UserCv entity
    UserCv userCv = UserCv.builder()
        .user(user)
        .cvUrl(cvUrl)
        .fileName(file.getOriginalFilename())
        .fileSize(file.getSize())
        .fileType(file.getContentType())
        .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
        .description(request.getDescription())
        .build();
    
    UserCv savedCv = userCvRepository.save(userCv);
    
    log.info("CV uploaded successfully: {}", savedCv.getId());
    return convertToResponse(savedCv);
  }

  public CvDtos.CvListResponse getUserCvs(Long userId) {
    log.info("Getting CVs for user: {}", userId);
    
    List<UserCv> cvs = userCvRepository.findByUserIdOrderByCreatedAtDesc(userId);
    Long totalCount = userCvRepository.countByUserId(userId);
    
    List<CvDtos.CvResponse> cvResponses = cvs.stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
    
    return CvDtos.CvListResponse.builder()
        .cvs(cvResponses)
        .totalCount(totalCount)
        .build();
  }

  public CvDtos.CvResponse getCvById(Long userId, Long cvId) {
    log.info("Getting CV by ID: {} for user: {}", cvId, userId);
    
    UserCv userCv = userCvRepository.findById(cvId)
        .orElseThrow(() -> new RuntimeException("CV not found"));
    
    if (!userCv.getUser().getId().equals(userId)) {
      throw new RuntimeException("CV does not belong to user");
    }
    
    return convertToResponse(userCv);
  }

  @Transactional
  public CvDtos.CvResponse updateCv(Long userId, Long cvId, CvDtos.CvUpdateRequest request) {
    log.info("Updating CV: {} for user: {}", cvId, userId);
    
    UserCv userCv = userCvRepository.findById(cvId)
        .orElseThrow(() -> new RuntimeException("CV not found"));
    
    if (!userCv.getUser().getId().equals(userId)) {
      throw new RuntimeException("CV does not belong to user");
    }
    
    // If setting as primary, unset other primary CVs
    if (Boolean.TRUE.equals(request.getIsPrimary())) {
      unsetPrimaryCvs(userId);
    }
    
    // Update fields
    if (request.getDescription() != null) {
      userCv.setDescription(request.getDescription());
    }
    if (request.getIsPrimary() != null) {
      userCv.setIsPrimary(request.getIsPrimary());
    }
    
    UserCv updatedCv = userCvRepository.save(userCv);
    
    log.info("CV updated successfully: {}", updatedCv.getId());
    return convertToResponse(updatedCv);
  }

  @Transactional
  public void deleteCv(Long userId, Long cvId) {
    log.info("Deleting CV: {} for user: {}", cvId, userId);
    
    UserCv userCv = userCvRepository.findById(cvId)
        .orElseThrow(() -> new RuntimeException("CV not found"));
    
    if (!userCv.getUser().getId().equals(userId)) {
      throw new RuntimeException("CV does not belong to user");
    }
    
    // Delete file from storage
    try {
      fileUploadService.deleteFile(userCv.getCvUrl());
    } catch (Exception e) {
      log.warn("Failed to delete file from storage: {}", e.getMessage());
    }
    
    userCvRepository.delete(userCv);
    
    log.info("CV deleted successfully: {}", cvId);
  }

  @Transactional
  public CvDtos.CvResponse setPrimaryCv(Long userId, Long cvId) {
    log.info("Setting primary CV: {} for user: {}", cvId, userId);
    
    UserCv userCv = userCvRepository.findById(cvId)
        .orElseThrow(() -> new RuntimeException("CV not found"));
    
    if (!userCv.getUser().getId().equals(userId)) {
      throw new RuntimeException("CV does not belong to user");
    }
    
    // Unset other primary CVs
    unsetPrimaryCvs(userId);
    
    // Set this CV as primary
    userCv.setIsPrimary(true);
    UserCv updatedCv = userCvRepository.save(userCv);
    
    log.info("Primary CV set successfully: {}", updatedCv.getId());
    return convertToResponse(updatedCv);
  }

  public Optional<CvDtos.CvResponse> getPrimaryCv(Long userId) {
    log.info("Getting primary CV for user: {}", userId);
    
    Optional<UserCv> primaryCv = userCvRepository.findPrimaryCvByUserId(userId);
    return primaryCv.map(this::convertToResponse);
  }

  private void unsetPrimaryCvs(Long userId) {
    List<UserCv> primaryCvs = userCvRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .filter(cv -> Boolean.TRUE.equals(cv.getIsPrimary()))
        .collect(Collectors.toList());
    
    for (UserCv cv : primaryCvs) {
      cv.setIsPrimary(false);
      userCvRepository.save(cv);
    }
  }

  private CvDtos.CvResponse convertToResponse(UserCv userCv) {
    return CvDtos.CvResponse.builder()
        .id(userCv.getId())
        .cvUrl(userCv.getCvUrl())
        .fileName(userCv.getFileName())
        .fileSize(userCv.getFileSize())
        .fileType(userCv.getFileType())
        .isPrimary(userCv.getIsPrimary())
        .description(userCv.getDescription())
        .createdAt(userCv.getCreatedAt())
        .updatedAt(userCv.getUpdatedAt())
        .build();
  }
}
