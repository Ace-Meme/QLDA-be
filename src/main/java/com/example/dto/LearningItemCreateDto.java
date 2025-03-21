package com.example.dto;

import com.example.model.LearningItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for creating a new learning item
 * Contains all required fields for creating a learning item
 */
public record LearningItemCreateDto(
    /**
     * Title of the learning item displayed to users
     * Must not be blank
     */
    @NotBlank(message = "Title is required")
    String title,
    
    /**
     * Type of learning item (LECTURE, READING, ASSIGNMENT, etc.)
     * Must not be null
     */
    @NotNull(message = "Type is required")
    LearningItemType type,
    
    /**
     * Rich text content of the learning item
     * Optional field that can be empty
     */
    String content,
    
    /**
     * Estimated duration to complete the learning item in minutes
     * Must be zero or positive
     */
    @PositiveOrZero(message = "Duration minutes must be positive or zero")
    Integer durationMinutes,
    
    /**
     * Display order of this item within its week
     * Must be zero or positive
     */
    @PositiveOrZero(message = "Order index must be positive or zero")
    Integer orderIndex,
    
    /**
     * ID of the course week this learning item belongs to
     * Must be a positive number
     */
    @NotNull(message = "Week ID is required")
    @Positive(message = "Week ID must be positive")
    Long weekId
) {
    public LearningItemCreateDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must be specified");
        }
        if (durationMinutes == null) {
            durationMinutes = 0;
        }
        if (orderIndex == null) {
            orderIndex = 0;
        }
        if (weekId == null || weekId <= 0) {
            throw new IllegalArgumentException("Valid week ID must be provided");
        }
    }
} 