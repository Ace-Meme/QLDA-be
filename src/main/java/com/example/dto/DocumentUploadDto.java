package com.example.dto;

/**
 * Data Transfer Object for document upload/update operations
 * Contains essential fields required for creating or updating a document
 */
public record DocumentUploadDto(
    /**
     * Title of the document displayed to users
     */
    String title,
    
    /**
     * Optional description of the document contents
     */
    String description,
    
    /**
     * Optional ID of learning item to associate this document with
     * (null if the document should be standalone)
     */
    Long learningItemId
) {
    public DocumentUploadDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Document title cannot be null or blank");
        }
    }
} 