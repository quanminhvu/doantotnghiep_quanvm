package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruiter_user_id", nullable = false)
  private User recruiter;

  @Column(nullable = false)
  private String title;

  @Column(name = "location")
  private String location;

  @Column(name = "employment_type")
  private String employmentType;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "requirements", columnDefinition = "TEXT")
  private String requirements;

  @Column(name = "benefits", columnDefinition = "TEXT")
  private String benefits;

  @Column(name = "salary_min")
  private Long salaryMin;

  @Column(name = "salary_max")
  private Long salaryMax;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (!active) {
      active = true;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}


