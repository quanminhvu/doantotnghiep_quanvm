package com.quanvm.applyin.dto;

import com.quanvm.applyin.entity.JobPosting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse {
    private Long id;
    private JobPosting jobPosting;
    private Instant savedAt;
}
