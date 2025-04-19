package com.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for associating a quiz bank with a learning item
 */
public record QuizBankLearningItemAssociationDto(
    /**
     * ID of the learning item to associate with the quiz bank
     * Must be a positive number
     */
    @NotNull(message = "Learning item ID is required")
    @Positive(message = "Learning item ID must be positive")
    Long learningItemId
) {
    public QuizBankLearningItemAssociationDto {
        if (learningItemId == null) {
            throw new IllegalArgumentException("Learning item ID must be specified");
        }
    }
} 