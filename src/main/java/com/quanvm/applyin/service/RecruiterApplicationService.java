package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.RecruiterApplicationDtos.*;
import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.ProfileUser;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.JobApplicationRepository;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.ProfileUserRepository;
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
public class RecruiterApplicationService {

  private final JobApplicationRepository jobApplicationRepository;
  private final JobPostingRepository jobPostingRepository;
  private final UserRepository userRepository;
  private final ProfileUserRepository profileUserRepository;

  public List<RecruiterApplicationResponse> getApplicationsForMyJobs(String recruiterEmail, Long jobId) {
    log.debug("[RECRUITER_APPLICATIONS][LIST] recruiterEmail={}, jobId={}", recruiterEmail, jobId);
    
    User recruiter = userRepository.findByEmail(recruiterEmail)
        .orElseThrow(() -> new RuntimeException("Recruiter not found"));
    
    List<JobApplication> applications;
    
    if (jobId != null) {
      // Get applications for specific job
      JobPosting job = jobPostingRepository.findById(jobId)
          .orElseThrow(() -> new RuntimeException("Job not found"));
      
      // Verify job belongs to recruiter
      if (!job.getRecruiter().getId().equals(recruiter.getId())) {
        throw new RuntimeException("Job does not belong to this recruiter");
      }
      
      applications = jobApplicationRepository.findByJobPostingOrderByCreatedAtDesc(job);
    } else {
      // Get all applications for recruiter's jobs
      List<JobPosting> recruiterJobs = jobPostingRepository.findByRecruiterOrderByCreatedAtDesc(recruiter);
      applications = jobApplicationRepository.findByJobPostingInOrderByCreatedAtDesc(recruiterJobs);
    }
    
    return applications.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public List<RecruiterApplicationResponse> getApplicationsForJob(String recruiterEmail, Long jobId) {
    log.debug("[RECRUITER_APPLICATIONS][LIST_BY_JOB] recruiterEmail={}, jobId={}", recruiterEmail, jobId);
    
    User recruiter = userRepository.findByEmail(recruiterEmail)
        .orElseThrow(() -> new RuntimeException("Recruiter not found"));
    
    JobPosting job = jobPostingRepository.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Job not found"));
    
    // Verify job belongs to recruiter
    if (!job.getRecruiter().getId().equals(recruiter.getId())) {
      throw new RuntimeException("Job does not belong to this recruiter");
    }
    
    List<JobApplication> applications = jobApplicationRepository.findByJobPostingOrderByCreatedAtDesc(job);
    
    return applications.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public RecruiterApplicationResponse getApplication(String recruiterEmail, Long applicationId) {
    log.debug("[RECRUITER_APPLICATIONS][GET] recruiterEmail={}, applicationId={}", recruiterEmail, applicationId);
    
    User recruiter = userRepository.findByEmail(recruiterEmail)
        .orElseThrow(() -> new RuntimeException("Recruiter not found"));
    
    JobApplication application = jobApplicationRepository.findById(applicationId)
        .orElseThrow(() -> new RuntimeException("Application not found"));
    
    // Verify application belongs to recruiter's job
    if (!application.getJobPosting().getRecruiter().getId().equals(recruiter.getId())) {
      throw new RuntimeException("Application does not belong to this recruiter's job");
    }
    
    return mapToResponse(application);
  }

  @Transactional
  public RecruiterApplicationResponse updateApplicationStatus(
      String recruiterEmail, 
      Long applicationId, 
      UpdateApplicationStatusRequest request
  ) {
    log.debug("[RECRUITER_APPLICATIONS][UPDATE_STATUS] recruiterEmail={}, applicationId={}, status={}", 
        recruiterEmail, applicationId, request.status());
    
    User recruiter = userRepository.findByEmail(recruiterEmail)
        .orElseThrow(() -> new RuntimeException("Recruiter not found"));
    
    JobApplication application = jobApplicationRepository.findById(applicationId)
        .orElseThrow(() -> new RuntimeException("Application not found"));
    
    // Verify application belongs to recruiter's job
    if (!application.getJobPosting().getRecruiter().getId().equals(recruiter.getId())) {
      throw new RuntimeException("Application does not belong to this recruiter's job");
    }
    
    // Update status
    try {
      JobApplication.Status newStatus = JobApplication.Status.valueOf(request.status());
      application.setStatus(newStatus);
      JobApplication saved = jobApplicationRepository.save(application);
      
      log.debug("[RECRUITER_APPLICATIONS][UPDATE_STATUS] success applicationId={}, newStatus={}", 
          applicationId, newStatus);
      
      return mapToResponse(saved);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid status: " + request.status());
    }
  }

  private RecruiterApplicationResponse mapToResponse(JobApplication application) {
    // Get candidate profile for phone number
    ProfileUser candidateProfile = profileUserRepository.findByUser(application.getCandidate())
        .orElse(null);
    
    return new RecruiterApplicationResponse(
        application.getId(),
        application.getJobPosting().getId(),
        application.getJobPosting().getTitle(),
        application.getCandidate().getFullName(),
        application.getCandidate().getEmail(),
        candidateProfile != null ? candidateProfile.getPhoneNumber() : null,
        application.getCvUrl(),
        application.getNote(), // Using note as cover letter
        application.getNote(),
        application.getStatus().name(),
        application.getApplicationNumber(),
        application.getCreatedAt(),
        application.getUpdatedAt()
    );
  }
}
