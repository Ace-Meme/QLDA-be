package com.example.dto;

import java.time.LocalDateTime;

public record QuizBankDTO(
    Long id,
    String title,
    String description,
    Long createdById,
    String createdByName,
    LocalDateTime creationDate,
    LocalDateTime lastModifiedDate,
    boolean active,
    Long questionCount
) {
    public QuizBankDTO {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be blank");
        }
        if (description == null) {
            description = "";
        }
    }
} 