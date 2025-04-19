package com.example.dto;

public record QuizBankUpdateDTO(
    String title,
    String description,
    Boolean active
) {
    // Removed compact constructor to allow null values for partial updates
    // Validation (e.g., non-blank title if provided) should be handled in the service layer.
} 