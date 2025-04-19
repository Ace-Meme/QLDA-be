package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for document upload requests
 */
public record DocumentUploadDto(
    /**
     * Title of the document displayed to users
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    String title,
    
    /**
     * Optional description of the document
     */
    String description,
    
    /**
     * ID of the learning item to associate this document with (optional)
     */
    Long learningItemId,
    
    /**
     * Indicates if this document should be treated as a video
     * If null, will be auto-determined from file content type
     */
    Boolean isVideo
) {} 