package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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
