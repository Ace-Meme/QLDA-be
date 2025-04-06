package com.example.dto;

public record QuizBankCreateDTO(
    String title,
    String description
) {
    public QuizBankCreateDTO {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be blank");
        }
        if (description == null) {
            description = "";
        }
    }
} 