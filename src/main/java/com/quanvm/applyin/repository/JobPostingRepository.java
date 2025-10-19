package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.JobPosting;
import com.quanvm.applyin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
  List<JobPosting> findByRecruiter(User recruiter);
  List<JobPosting> findByRecruiterOrderByCreatedAtDesc(User recruiter);
  List<JobPosting> findByActiveTrueOrderByCreatedAtDesc();
  
  // Pagination support
  Page<JobPosting> findByActiveTrue(Pageable pageable);
  Page<JobPosting> findByRecruiter(User recruiter, Pageable pageable);
}


