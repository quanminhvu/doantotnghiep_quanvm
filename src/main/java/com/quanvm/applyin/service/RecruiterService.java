package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.RecruiterDtos.*;
import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.RecruiterProfile;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.JobApplicationRepository;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.RecruiterProfileRepository;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.util.constant.UserEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterService {

  private final RecruiterProfileRepository recruiterProfileRepository;
  private final JobPostingRepository jobPostingRepository;
  private final JobApplicationRepository jobApplicationRepository;
  private final UserRepository userRepository;

  private void ensureRecruiter(User user) {
    if (user.getRole() != UserEnum.Role.RECRUITER && user.getRole() != UserEnum.Role.ADMIN) {
      throw new AccessDeniedException("Chỉ RECRUITER/ADMIN được phép thao tác");
    }
  }

  public RecruiterProfileResponse getMyProfile(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    RecruiterProfile profile = recruiterProfileRepository.findByUser(user)
        .orElseGet(() -> recruiterProfileRepository.save(RecruiterProfile.builder()
            .user(user)
            .companyName(user.getFullName())
            .build()));
    return mapProfile(profile);
  }

  @Transactional
  public RecruiterProfileResponse upsertMyProfile(String email, RecruiterProfileRequest req) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    RecruiterProfile profile = recruiterProfileRepository.findByUser(user)
        .orElseGet(() -> RecruiterProfile.builder().user(user).build());

    profile.setCompanyName(req.companyName());
    profile.setCompanyWebsite(req.companyWebsite());
    profile.setCompanyAddress(req.companyAddress());
    profile.setCompanySize(req.companySize());
    profile.setAbout(req.about());
    profile.setLogoUrl(req.logoUrl());
    profile.setRecruiterName(req.recruiterName());
    profile.setRecruiterTitle(req.recruiterTitle());
    profile.setRecruiterPhone(req.recruiterPhone());
    profile.setRecruiterEmail(req.recruiterEmail());
    profile.setRecruiterLinkedin(req.recruiterLinkedin());
    profile.setRecruiterAbout(req.recruiterAbout());
    profile.setRecruiterAvatarUrl(req.recruiterAvatarUrl());
    profile.setUpdatedAt(Instant.now());
    recruiterProfileRepository.save(profile);
    return mapProfile(profile);
  }

  public List<JobPostingResponse> listMyJobs(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    return jobPostingRepository.findByRecruiter(user).stream().map(this::mapJob).collect(Collectors.toList());
  }

  @Transactional
  public JobPostingResponse createJob(String email, JobPostingRequest req) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    Instant now = Instant.now();
    JobPosting job = JobPosting.builder()
        .recruiter(user)
        .title(req.title())
        .location(req.location())
        .employmentType(req.employmentType())
        .description(req.description())
        .requirements(req.requirements())
        .benefits(req.benefits())
        .salaryMin(req.salaryMin())
        .salaryMax(req.salaryMax())
        .active(Boolean.TRUE.equals(req.active()))
        .createdAt(now)
        .updatedAt(now)
        .build();
    jobPostingRepository.save(job);
    return mapJob(job);
  }

  @Transactional
  public JobPostingResponse updateJob(String email, Long jobId, JobPostingRequest req) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    JobPosting job = jobPostingRepository.findById(jobId).orElseThrow();
    if (!job.getRecruiter().getId().equals(user.getId())) {
      throw new AccessDeniedException("Không có quyền sửa bài đăng này");
    }
    job.setTitle(req.title());
    job.setLocation(req.location());
    job.setEmploymentType(req.employmentType());
    job.setDescription(req.description());
    job.setRequirements(req.requirements());
    job.setBenefits(req.benefits());
    job.setSalaryMin(req.salaryMin());
    job.setSalaryMax(req.salaryMax());
    if (req.active() != null) job.setActive(req.active());
    job.setUpdatedAt(Instant.now());
    jobPostingRepository.save(job);
    return mapJob(job);
  }

  @Transactional
  public void deleteJob(String email, Long jobId) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    JobPosting job = jobPostingRepository.findById(jobId).orElseThrow();
    if (!job.getRecruiter().getId().equals(user.getId())) {
      throw new AccessDeniedException("Không có quyền xóa bài đăng này");
    }
    jobPostingRepository.delete(job);
  }

  public List<JobApplicationResponse> listApplicationsForMyCompany(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    return jobApplicationRepository.findByJobPostingRecruiter(user).stream()
        .map(this::mapApplication)
        .collect(Collectors.toList());
  }

  @Transactional
  public JobApplicationResponse updateApplicationStatus(String email, Long applicationId, String status, String note) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    JobApplication app = jobApplicationRepository.findById(applicationId).orElseThrow();
    if (!app.getJobPosting().getRecruiter().getId().equals(user.getId())) {
      throw new AccessDeniedException("Không có quyền cập nhật đơn này");
    }
    app.setStatus(JobApplication.Status.valueOf(status.toUpperCase()));
    app.setNote(note);
    app.setUpdatedAt(Instant.now());
    jobApplicationRepository.save(app);
    return mapApplication(app);
  }

  private RecruiterProfileResponse mapProfile(RecruiterProfile p) {
    return new RecruiterProfileResponse(
        p.getId(), p.getCompanyName(), p.getCompanyWebsite(), p.getCompanyAddress(),
        p.getCompanySize(), p.getAbout(), p.getLogoUrl(),
        p.getRecruiterName(), p.getRecruiterTitle(), p.getRecruiterPhone(), p.getRecruiterEmail(),
        p.getRecruiterLinkedin(), p.getRecruiterAbout(), p.getRecruiterAvatarUrl(),
        p.getCreatedAt(), p.getUpdatedAt());
  }

  @Transactional
  public RecruiterProfileResponse updateLogoUrl(String email, String logoUrl) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    RecruiterProfile profile = recruiterProfileRepository.findByUser(user)
        .orElseGet(() -> recruiterProfileRepository.save(RecruiterProfile.builder().user(user).companyName(user.getFullName()).build()));
    profile.setLogoUrl(logoUrl);
    profile.setUpdatedAt(Instant.now());
    recruiterProfileRepository.save(profile);
    return mapProfile(profile);
  }

  @Transactional
  public RecruiterProfileResponse updateAvatarUrl(String email, String avatarUrl) {
    User user = userRepository.findByEmail(email).orElseThrow();
    ensureRecruiter(user);
    RecruiterProfile profile = recruiterProfileRepository.findByUser(user)
        .orElseGet(() -> recruiterProfileRepository.save(RecruiterProfile.builder().user(user).companyName(user.getFullName()).build()));
    profile.setRecruiterAvatarUrl(avatarUrl);
    profile.setUpdatedAt(Instant.now());
    recruiterProfileRepository.save(profile);
    return mapProfile(profile);
  }

  private JobPostingResponse mapJob(JobPosting j) {
    return new JobPostingResponse(
        j.getId(), j.getTitle(), j.getLocation(), j.getEmploymentType(), j.getDescription(),
        j.getRequirements(), j.getBenefits(), j.getSalaryMin(), j.getSalaryMax(), j.isActive(),
        j.getCreatedAt(), j.getUpdatedAt());
  }

  private JobApplicationResponse mapApplication(JobApplication a) {
    return new JobApplicationResponse(
        a.getId(), a.getJobPosting().getId(), a.getCandidate().getId(), a.getCvUrl(),
        a.getStatus().name(), a.getNote(), a.getCreatedAt(), a.getUpdatedAt());
  }
}


