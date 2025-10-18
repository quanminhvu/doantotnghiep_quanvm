package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.JobApplication;
import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
  List<JobApplication> findByJobPosting(JobPosting posting);
  List<JobApplication> findByJobPostingRecruiter(User recruiter);
  List<JobApplication> findByCandidateOrderByCreatedAtDesc(User candidate);
  JobApplication findByIdAndCandidate(Long id, User candidate);
  boolean existsByCandidateAndJobPosting(User candidate, JobPosting jobPosting);
}


