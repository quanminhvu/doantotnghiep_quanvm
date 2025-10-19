package com.quanvm.applyin.dto;

import lombok.Builder;

import java.util.List;

public class PaginationDto {

    @Builder
    public record PaginationRequest(
        int page,
        int size,
        String sortBy,
        String sortDirection
    ) {
        public PaginationRequest {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;
            if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "createdAt";
            if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "desc";
        }
    }

    @Builder
    public record PaginationResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
    ) {}

    @Builder
    public record PaginationMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
    ) {}
}
