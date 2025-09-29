package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.ProfileUser;
import com.quanvm.applyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileUserRepository extends JpaRepository<ProfileUser, Long> {
    Optional<ProfileUser> findByUser(User user);
    Optional<ProfileUser> findByUserId(Long userId);
}
