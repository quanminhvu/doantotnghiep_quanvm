package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.UserCv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCvRepository extends JpaRepository<UserCv, Long> {
  
  List<UserCv> findByUserIdOrderByCreatedAtDesc(Long userId);
  
  Optional<UserCv> findByUserIdAndIsPrimaryTrue(Long userId);
  
  @Query("SELECT uc FROM UserCv uc WHERE uc.user.id = :userId AND uc.isPrimary = true")
  Optional<UserCv> findPrimaryCvByUserId(@Param("userId") Long userId);
  
  @Query("SELECT COUNT(uc) FROM UserCv uc WHERE uc.user.id = :userId")
  Long countByUserId(@Param("userId") Long userId);
  
  void deleteByUserIdAndId(Long userId, Long cvId);
}
