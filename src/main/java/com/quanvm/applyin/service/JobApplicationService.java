package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.JobApplicationDtos.*;
import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.RecruiterProfile;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.JobApplicationRepository;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.RecruiterProfileRepository;
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
  private final RecruiterProfileRepository recruiterProfileRepository;

  @Transactional
  public JobApplicationResponse applyForJob(String userEmail, JobApplicationRequest request) {
    log.debug("[APPLICATION][APPLY] userEmail={}, jobId={}", userEmail, request.jobId());
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobPosting job = jobPostingRepository.findById(request.jobId())
        .orElseThrow(() -> new RuntimeException("Job not found"));
    
    // Check application limit (max 3 times)
    Integer applicationCount = jobApplicationRepository.countByCandidateAndJobPosting(candidate, job);
    if (applicationCount >= 3) {
      throw new RuntimeException("Bạn đã hết lượt ứng tuyển cho công việc này (tối đa 3 lần)");
    }
    
    // Get next application number
    Integer nextApplicationNumber = applicationCount + 1;
    
    JobApplication application = JobApplication.builder()
        .candidate(candidate)
        .jobPosting(job)
        .cvUrl(request.cvUrl())
        .note(request.note())
        .applicationNumber(nextApplicationNumber)
        .status(JobApplication.Status.SUBMITTED)
        .build();
    
    JobApplication saved = jobApplicationRepository.save(application);
    log.debug("[APPLICATION][APPLY] success applicationId={}, applicationNumber={}", saved.getId(), nextApplicationNumber);
    
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

  public List<JobApplicationResponse> getApplicationsForJob(String userEmail, Long jobId) {
    log.debug("[APPLICATION][LIST_BY_JOB] userEmail={}, jobId={}", userEmail, jobId);
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobPosting job = jobPostingRepository.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Job not found"));
    
    List<JobApplication> applications = jobApplicationRepository.findByCandidateAndJobPostingOrderByApplicationNumberDesc(candidate, job);
    
    return applications.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public Integer getApplicationCountForJob(String userEmail, Long jobId) {
    log.debug("[APPLICATION][COUNT] userEmail={}, jobId={}", userEmail, jobId);
    
    User candidate = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    JobPosting job = jobPostingRepository.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Job not found"));
    
    return jobApplicationRepository.countByCandidateAndJobPosting(candidate, job);
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
    // Get recruiter profile for company information
    RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUser(application.getJobPosting().getRecruiter())
        .orElse(null);
    
    return JobApplicationResponse.builder()
        .id(application.getId())
        .jobId(application.getJobPosting().getId())
        .jobTitle(application.getJobPosting().getTitle())
        .companyName(recruiterProfile != null ? recruiterProfile.getCompanyName() : "Unknown Company")
        .cvUrl(application.getCvUrl())
        .coverLetter(application.getNote()) // Using note as cover letter
        .note(application.getNote())
        .status(application.getStatus().name())
        .applicationNumber(application.getApplicationNumber())
        .createdAt(application.getCreatedAt())
        .updatedAt(application.getUpdatedAt())
        .build();
  }
}
