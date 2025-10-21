package com.quanvm.applyin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import com.quanvm.applyin.util.ListToJsonStringConverter;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class JobPosting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruiter_user_id", nullable = false)
  @JsonIgnore
  private User recruiter;

  @Column(nullable = false)
  private String title;

  @Column(name = "location")
  private String location;

  @Column(name = "employment_type")
  private String employmentType;

  @Convert(converter = ListToJsonStringConverter.class)
  @Column(name = "description", columnDefinition = "TEXT")
  private List<String> description;

  @Convert(converter = ListToJsonStringConverter.class)
  @Column(name = "requirements", columnDefinition = "TEXT")
  private List<String> requirements;

  @Convert(converter = ListToJsonStringConverter.class)
  @Column(name = "benefits", columnDefinition = "TEXT")
  private List<String> benefits;

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


