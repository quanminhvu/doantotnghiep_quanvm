package com.quanvm.applyin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

public class CvDtos {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CvResponse {
    private Long id;
    private String cvUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Boolean isPrimary;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CvUploadRequest {
    private String description;
    private Boolean isPrimary;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CvUpdateRequest {
    private String description;
    private Boolean isPrimary;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CvListResponse {
    private java.util.List<CvResponse> cvs;
    private Long totalCount;
  }
}
