package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "recruiter_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "company_website")
  private String companyWebsite;

  @Column(name = "company_address")
  private String companyAddress;

  @Column(name = "company_size")
  private String companySize;

  @Column(name = "about", columnDefinition = "TEXT")
  private String about;

  @Column(name = "logo_url")
  private String logoUrl;

  // Recruiter personal profile
  @Column(name = "recruiter_name")
  private String recruiterName;

  @Column(name = "recruiter_title")
  private String recruiterTitle;

  @Column(name = "recruiter_phone")
  private String recruiterPhone;

  @Column(name = "recruiter_email")
  private String recruiterEmail;

  @Column(name = "recruiter_linkedin")
  private String recruiterLinkedin;

  @Column(name = "recruiter_about", columnDefinition = "TEXT")
  private String recruiterAbout;

  @Column(name = "recruiter_avatar_url")
  private String recruiterAvatarUrl;

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


