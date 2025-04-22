package com.example.dto;

import com.example.model.LearningItemType;
import java.util.List;

/**
 * Data Transfer Object for Learning Item information
 * Contains all learning item details for displaying to users
 */
public record LearningItemDto(
    /**
     * Unique identifier for the learning item
     */
    Long id,
    
    /**
     * Title of the learning item displayed to users
     */
    String title,
    
    /**
     * Type of learning item (LECTURE, READING, ASSIGNMENT, etc.)
     */
    LearningItemType type,
    
    /**
     * Rich text content of the learning item
     */
    String content,
    
    /**
     * Estimated duration to complete the learning item in minutes
     */
    Integer durationMinutes,
    
    /**
     * Display order of this item within its week
     */
    Integer orderIndex,
    
    /**
     * ID of the course week this learning item belongs to
     */
    Long weekId,
    
    /**
     * Title of the course week this learning item belongs to
     */
    String weekTitle,
    
    /**
     * List of documents associated with this learning item
     */
    List<DocumentDto> documents,
    
    /**
     * Quiz bank associated with this learning item (only for QUIZ type)
     */
    QuizBankDTO quizBank
) {
    public LearningItemDto {
        if (documents == null) {
            documents = List.of();
        }
    }
}