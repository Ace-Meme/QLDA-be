package com.example.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequestDto(
        @NotNull(message = "Course ID cannot be null") Long courseId) {
    public EnrollmentRequestDto {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be a positive number.");
        }
    }
} 