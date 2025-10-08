package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import com.quanvm.applyin.util.constant.UserEnum;

@Entity
@Table(name = "profile_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Column(name = "cv_url")
  private String cvUrl;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Column(name = "date_of_birth")
  private Instant dateOfBirth;

  @Column(name = "address")
  private String address;

  @Column(name = "linkedin_url")
  private String linkedinUrl;

  @Column(name = "github_url")
  private String githubUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private UserEnum.Gender gender;

  @Column(name = "skills_json", columnDefinition = "TEXT")
  private String skillsJson;

  @Column(name = "experiences_json", columnDefinition = "TEXT")
  private String experiencesJson;

  @Column(name = "education_json", columnDefinition = "TEXT")
  private String educationJson;

  @Column(name = "job_seeking")
  private Boolean jobSeeking;

  @Column(name = "cv_upload_count")
  private Integer cvUploadCount;

  @Column(name = "cover_letter", columnDefinition = "TEXT")
  private String coverLetter;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}
