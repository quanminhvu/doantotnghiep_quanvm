package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.SavedJob;
import com.quanvm.applyin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    
    /**
     * Kiểm tra xem user đã lưu job này chưa
     */
    boolean existsByUserIdAndJobPostingId(Long userId, Long jobPostingId);
    
    /**
     * Tìm saved job theo user và job posting
     */
    Optional<SavedJob> findByUserIdAndJobPostingId(Long userId, Long jobPostingId);
    
    /**
     * Lấy danh sách saved jobs của user với pagination
     */
    @Query("SELECT sj FROM SavedJob sj " +
           "WHERE sj.user.id = :userId " +
           "ORDER BY sj.savedAt DESC")
    Page<SavedJob> findByUserIdWithJobPosting(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Đếm số lượng saved jobs của user
     */
    long countByUserId(Long userId);
    
    /**
     * Xóa saved job theo user và job posting
     */
    void deleteByUserIdAndJobPostingId(Long userId, Long jobPostingId);
}
