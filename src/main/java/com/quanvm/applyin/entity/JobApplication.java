package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_posting_id", nullable = false)
  private JobPosting jobPosting;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_user_id", nullable = false)
  private User candidate;

  @Column(name = "cv_url")
  private String cvUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (status == null) {
      status = Status.SUBMITTED;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  public enum Status {
    SUBMITTED,
    IN_REVIEW,
    INTERVIEW,
    OFFER,
    REJECTED,
    WITHDRAWN
  }
}


