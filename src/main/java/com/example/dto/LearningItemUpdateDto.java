package com.example.dto;

import com.example.model.LearningItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for updating an existing learning item
 * Contains all fields that can be updated for a learning item
 */
public record LearningItemUpdateDto(
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
    Integer orderIndex
) {
    public LearningItemUpdateDto {
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
    }
} 