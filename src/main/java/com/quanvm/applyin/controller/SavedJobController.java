package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.SavedJobResponse;
import com.quanvm.applyin.entity.SavedJob;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
@Slf4j
public class SavedJobController {

    private final SavedJobService savedJobService;
    private final UserRepository userRepository;
    
    /**
     * Lưu công việc yêu thích
     */
    @PostMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> saveJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            boolean success = savedJobService.saveJob(userId, jobId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Đã lưu công việc" : "Không thể lưu công việc");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error saving job {}", jobId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lưu công việc");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Bỏ lưu công việc yêu thích
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> unsaveJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            boolean success = savedJobService.unsaveJob(userId, jobId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Đã bỏ lưu công việc" : "Không thể bỏ lưu công việc");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error unsaving job {}", jobId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi bỏ lưu công việc");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Kiểm tra xem job đã được lưu chưa
     */
    @GetMapping("/{jobId}/status")
    public ResponseEntity<Map<String, Object>> getJobSavedStatus(
            @PathVariable Long jobId,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            boolean isSaved = savedJobService.isJobSaved(userId, jobId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isSaved", isSaved);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error checking job saved status {}", jobId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("isSaved", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lấy danh sách công việc đã lưu
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            Pageable pageable = PageRequest.of(page, size);
            Page<SavedJob> savedJobs = savedJobService.getSavedJobs(userId, pageable);
            
            // Convert to DTO to avoid serialization issues
            List<SavedJobResponse> savedJobResponses = savedJobs.getContent().stream()
                    .map(savedJob -> SavedJobResponse.builder()
                            .id(savedJob.getId())
                            .jobPosting(savedJob.getJobPosting())
                            .savedAt(savedJob.getSavedAt())
                            .build())
                    .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("savedJobs", savedJobResponses);
            response.put("currentPage", savedJobs.getNumber());
            response.put("totalPages", savedJobs.getTotalPages());
            response.put("totalElements", savedJobs.getTotalElements());
            response.put("hasNext", savedJobs.hasNext());
            response.put("hasPrevious", savedJobs.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting saved jobs", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Có lỗi xảy ra khi lấy danh sách công việc đã lưu");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lấy danh sách ID các job đã lưu
     */
    @GetMapping("/ids")
    public ResponseEntity<Map<String, Object>> getSavedJobIds(Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Long> savedJobIds = savedJobService.getSavedJobIds(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("savedJobIds", savedJobIds);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting saved job IDs", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Có lỗi xảy ra khi lấy danh sách ID công việc đã lưu");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lấy số lượng công việc đã lưu
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getSavedJobsCount(Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            long count = savedJobService.getSavedJobsCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting saved jobs count", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Có lỗi xảy ra khi đếm số lượng công việc đã lưu");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        try {
            String email = authentication.getName();
            // Tìm user ID từ email
            return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElse(1L); // Fallback to user ID 1 for testing
        } catch (Exception e) {
            // Fallback to user ID 1 for testing
            return 1L;
        }
    }
    
    /**
     * Test endpoint để kiểm tra database connection
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test database connection by counting users
            long userCount = userRepository.count();
            response.put("success", true);
            response.put("message", "Database connection OK");
            response.put("userCount", userCount);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Database error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
