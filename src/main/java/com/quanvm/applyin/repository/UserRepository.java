package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByResetToken(String resetToken);
  boolean existsByEmail(String email);
}


