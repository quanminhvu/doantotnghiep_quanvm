package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.JobApplicationDtos.*;
import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.JobApplicationRepository;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {

  private final JobApplicationRepository jobApplicationRepository;
  private final JobPostingRepository jobPostingRepository;
  private final UserRepository userRepository;

  @Transactional
  public JobApplicationResponse applyForJob(String userEmail, JobApplicationRequest request) {
    log.debug("[APPLICATION][APPLY] userEmail={}, jobId={}", userEmail, request.jobId());
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobPosting job = jobPostingRepository.findById(request.jobId())
        .orElseThrow(() -> new RuntimeException("Job not found"));
    
    // Check if already applied
    if (jobApplicationRepository.existsByCandidateAndJobPosting(candidate, job)) {
      throw new RuntimeException("You have already applied for this job");
    }
    
    JobApplication application = JobApplication.builder()
        .candidate(candidate)
        .jobPosting(job)
        .cvUrl(request.cvUrl())
        .note(request.note())
        .status(JobApplication.Status.SUBMITTED)
        .build();
    
    JobApplication saved = jobApplicationRepository.save(application);
    log.debug("[APPLICATION][APPLY] success applicationId={}", saved.getId());
    
    return mapToResponse(saved);
  }

  public List<JobApplicationResponse> getMyApplications(String userEmail) {
    log.debug("[APPLICATION][LIST] userEmail={}", userEmail);
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    List<JobApplication> applications = jobApplicationRepository.findByCandidateOrderByCreatedAtDesc(candidate);
    
    return applications.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public JobApplicationResponse getApplication(String userEmail, Long applicationId) {
    log.debug("[APPLICATION][GET] userEmail={}, applicationId={}", userEmail, applicationId);
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobApplication application = jobApplicationRepository.findByIdAndCandidate(applicationId, candidate)
        .orElseThrow(() -> new RuntimeException("Application not found"));
    
    return mapToResponse(application);
  }

  @Transactional
  public JobApplicationResponse withdrawApplication(String userEmail, Long applicationId) {
    log.debug("[APPLICATION][WITHDRAW] userEmail={}, applicationId={}", userEmail, applicationId);
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobApplication application = jobApplicationRepository.findByIdAndCandidate(applicationId, candidate)
        .orElseThrow(() -> new RuntimeException("Application not found"));
    
    if (application.getStatus() != JobApplication.Status.SUBMITTED) {
      throw new RuntimeException("Cannot withdraw application with status: " + application.getStatus());
    }
    
    application.setStatus(JobApplication.Status.WITHDRAWN);
    JobApplication saved = jobApplicationRepository.save(application);
    
    log.debug("[APPLICATION][WITHDRAW] success applicationId={}", saved.getId());
    return mapToResponse(saved);
  }

  private JobApplicationResponse mapToResponse(JobApplication application) {
    return JobApplicationResponse.builder()
        .id(application.getId())
        .jobId(application.getJobPosting().getId())
        .jobTitle(application.getJobPosting().getTitle())
        .companyName(application.getJobPosting().getRecruiter().getRecruiterProfile().getCompanyName())
        .cvUrl(application.getCvUrl())
        .coverLetter(application.getNote()) // Using note as cover letter
        .note(application.getNote())
        .status(application.getStatus().name())
        .createdAt(application.getCreatedAt())
        .updatedAt(application.getUpdatedAt())
        .build();
  }
}
