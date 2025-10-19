package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.PaginationDto.PaginationRequest;
import com.quanvm.applyin.dto.PaginationDto.PaginationResponse;
import com.quanvm.applyin.dto.RecruiterDtos.JobPostingResponse;
import com.quanvm.applyin.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  @GetMapping
  public ResponseEntity<List<JobPostingResponse>> listActiveJobs() {
    return ResponseEntity.ok(jobService.listActiveJobs());
  }

  @GetMapping("/paginated")
  public ResponseEntity<PaginationResponse<JobPostingResponse>> listActiveJobsPaginated(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    PaginationRequest request = PaginationRequest.builder()
        .page(page)
        .size(size)
        .sortBy(sortBy)
        .sortDirection(sortDirection)
        .build();
    
    return ResponseEntity.ok(jobService.listActiveJobsPaginated(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<JobPostingResponse> getJobById(@PathVariable Long id) {
    return ResponseEntity.ok(jobService.getJobById(id));
  }
}
