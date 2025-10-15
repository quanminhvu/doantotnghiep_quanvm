package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.RecruiterProfile;
import com.quanvm.applyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
  Optional<RecruiterProfile> findByUser(User user);
  Optional<RecruiterProfile> findByUserId(Long userId);
}


