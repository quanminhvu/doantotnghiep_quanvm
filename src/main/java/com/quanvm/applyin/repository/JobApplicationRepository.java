package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
  List<JobApplication> findByJobPosting(JobPosting posting);
  List<JobApplication> findByJobPostingRecruiter(User recruiter);
  List<JobApplication> findByCandidateOrderByCreatedAtDesc(User candidate);
  Optional<JobApplication> findByIdAndCandidate(Long id, User candidate);
  boolean existsByCandidateAndJobPosting(User candidate, JobPosting jobPosting);
  List<JobApplication> findByCandidateAndJobPostingOrderByApplicationNumberDesc(User candidate, JobPosting jobPosting);
  Integer countByCandidateAndJobPosting(User candidate, JobPosting jobPosting);
  Optional<JobApplication> findByCandidateAndJobPostingAndApplicationNumber(User candidate, JobPosting jobPosting, Integer applicationNumber);
  
  // Recruiter queries
  List<JobApplication> findByJobPostingOrderByCreatedAtDesc(JobPosting posting);
  List<JobApplication> findByJobPostingInOrderByCreatedAtDesc(List<JobPosting> postings);
}


