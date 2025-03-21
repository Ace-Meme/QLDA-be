package com.example.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Document information
 * Contains all document details including metadata and file information
 */
public record DocumentDto(
    /**
     * Unique identifier for the document
     */
    Long id,
    
    /**
     * Title of the document displayed to users
     */
    String title,
    
    /**
     * Original filename of the uploaded document
     */
    String fileName,
    
    /**
     * MIME type of the document (e.g., application/pdf)
     */
    String contentType,
    
    /**
     * Size of the document file in bytes
     */
    Long fileSize,
    
    /**
     * URL where the document can be accessed or downloaded
     */
    String fileUrl,
    
    /**
     * Optional description of the document contents
     */
    String description,
    
    /**
     * Timestamp when the document was uploaded
     */
    LocalDateTime uploadedAt,
    
    /**
     * ID of the user who uploaded the document
     */
    Long uploadedById,
    
    /**
     * Name of the user who uploaded the document
     */
    String uploadedByName,
    
    /**
     * ID of the learning item this document is associated with (null if standalone)
     */
    Long learningItemId
) {
    public DocumentDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Document title cannot be null or blank");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("File URL cannot be null or blank");
        }
    }
} 