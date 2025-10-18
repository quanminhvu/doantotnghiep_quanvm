package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.RecruiterDtos.JobPostingResponse;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.RecruiterProfile;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

  private final JobPostingRepository jobPostingRepository;
  private final RecruiterProfileRepository recruiterProfileRepository;

  public List<JobPostingResponse> listActiveJobs() {
    List<JobPosting> jobs = jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc();
    return jobs.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public JobPostingResponse getJobById(Long id) {
    JobPosting job = jobPostingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    return toResponse(job);
  }

  private JobPostingResponse toResponse(JobPosting job) {
    // Get recruiter profile for company information
    RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(job.getRecruiter().getId())
        .orElse(null);
    
    return new JobPostingResponse(
        job.getId(),
        job.getTitle(),
        job.getLocation(),
        job.getEmploymentType(),
        job.getDescription(),
        job.getRequirements(),
        job.getBenefits(),
        job.getSalaryMin(),
        job.getSalaryMax(),
        job.isActive(),
        // Company information
        recruiterProfile != null ? recruiterProfile.getCompanyName() : "Unknown Company",
        recruiterProfile != null ? recruiterProfile.getLogoUrl() : null,
        recruiterProfile != null ? recruiterProfile.getCompanyAddress() : null,
        recruiterProfile != null ? recruiterProfile.getCompanySize() : null,
        recruiterProfile != null ? recruiterProfile.getCompanyWebsite() : null,
        job.getCreatedAt(),
        job.getUpdatedAt()
    );
  }
}
