package com.quanvm.applyin.controller;

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

  @GetMapping("/{id}")
  public ResponseEntity<JobPostingResponse> getJobById(@PathVariable Long id) {
    return ResponseEntity.ok(jobService.getJobById(id));
  }
}
