package com.example.dto;

public record QuizBankUpdateDTO(
    String title,
    String description,
    boolean active
) {
    public QuizBankUpdateDTO {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be blank");
        }
        if (description == null) {
            description = "";
        }
    }
} 