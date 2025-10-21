package com.quanvm.applyin.service;

import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.SavedJob;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.repository.JobPostingRepository;
import com.quanvm.applyin.repository.SavedJobRepository;
import com.quanvm.applyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedJobService {
    
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    
    /**
     * Lưu công việc yêu thích
     */
    @Transactional
    public boolean saveJob(Long userId, Long jobPostingId) {
        try {
            // Kiểm tra user và job posting có tồn tại không
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<JobPosting> jobOpt = jobPostingRepository.findById(jobPostingId);
            
            if (userOpt.isEmpty() || jobOpt.isEmpty()) {
                log.warn("User {} or JobPosting {} not found", userId, jobPostingId);
                return false;
            }
            
            // Kiểm tra đã lưu chưa
            if (savedJobRepository.existsByUserIdAndJobPostingId(userId, jobPostingId)) {
                log.info("Job {} already saved by user {}", jobPostingId, userId);
                return true; // Đã lưu rồi, coi như thành công
            }
            
            // Tạo saved job mới
            SavedJob savedJob = new SavedJob();
            savedJob.setUser(userOpt.get());
            savedJob.setJobPosting(jobOpt.get());
            
            savedJobRepository.save(savedJob);
            log.info("Job {} saved by user {}", jobPostingId, userId);
            return true;
            
        } catch (Exception e) {
            log.error("Error saving job {} for user {}", jobPostingId, userId, e);
            return false;
        }
    }
    
    /**
     * Bỏ lưu công việc yêu thích
     */
    @Transactional
    public boolean unsaveJob(Long userId, Long jobPostingId) {
        try {
            if (!savedJobRepository.existsByUserIdAndJobPostingId(userId, jobPostingId)) {
                log.info("Job {} not saved by user {}", jobPostingId, userId);
                return true; // Chưa lưu, coi như thành công
            }
            
            savedJobRepository.deleteByUserIdAndJobPostingId(userId, jobPostingId);
            log.info("Job {} unsaved by user {}", jobPostingId, userId);
            return true;
            
        } catch (Exception e) {
            log.error("Error unsaving job {} for user {}", jobPostingId, userId, e);
            return false;
        }
    }
    
    /**
     * Kiểm tra xem user đã lưu job này chưa
     */
    public boolean isJobSaved(Long userId, Long jobPostingId) {
        return savedJobRepository.existsByUserIdAndJobPostingId(userId, jobPostingId);
    }
    
    /**
     * Lấy danh sách công việc đã lưu của user
     */
    public Page<SavedJob> getSavedJobs(Long userId, Pageable pageable) {
        Page<SavedJob> savedJobs = savedJobRepository.findByUserIdWithJobPosting(userId, pageable);
        // Force eager loading để tránh lazy loading proxy
        savedJobs.getContent().forEach(savedJob -> {
            if (savedJob.getJobPosting() != null) {
                savedJob.getJobPosting().getId();
                savedJob.getJobPosting().getTitle();
                savedJob.getJobPosting().getLocation();
                savedJob.getJobPosting().getEmploymentType();
                savedJob.getJobPosting().getDescription();
                savedJob.getJobPosting().getRequirements();
                savedJob.getJobPosting().getBenefits();
                savedJob.getJobPosting().getSalaryMin();
                savedJob.getJobPosting().getSalaryMax();
                savedJob.getJobPosting().isActive();
                savedJob.getJobPosting().getCompanyName();
                savedJob.getJobPosting().getCompanyLogoUrl();
                savedJob.getJobPosting().getCompanyAddress();
                savedJob.getJobPosting().getCompanySize();
                savedJob.getJobPosting().getCompanyWebsite();
                savedJob.getJobPosting().getCreatedAt();
                savedJob.getJobPosting().getUpdatedAt();
            }
        });
        return savedJobs;
    }
    
    /**
     * Lấy danh sách ID các job đã lưu của user
     */
    public List<Long> getSavedJobIds(Long userId) {
        return savedJobRepository.findByUserIdWithJobPosting(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(savedJob -> savedJob.getJobPosting().getId())
                .toList();
    }
    
    /**
     * Đếm số lượng công việc đã lưu
     */
    public long getSavedJobsCount(Long userId) {
        return savedJobRepository.countByUserId(userId);
    }
}
