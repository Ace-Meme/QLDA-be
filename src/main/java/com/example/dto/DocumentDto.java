package com.example.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Document information
 * Contains all document details for displaying to users
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
     * Original filename of the uploaded file
     */
    String fileName,
    
    /**
     * MIME type of the file
     */
    String contentType,
    
    /**
     * Size of the file in bytes
     */
    Long fileSize,
    
    /**
     * URL where the file can be accessed/downloaded
     */
    String fileUrl,
    
    /**
     * Optional description of the document
     */
    String description,
    
    /**
     * When the document was uploaded
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
     * ID of the learning item this document is associated with (if any)
     */
    Long learningItemId,
    
    /**
     * Indicates if this document is a video file
     */
    Boolean isVideo
) {
    /**
     * Constructor with default isVideo value
     */
    public DocumentDto(
            Long id, String title, String fileName, String contentType, 
            Long fileSize, String fileUrl, String description, 
            LocalDateTime uploadedAt, Long uploadedById, 
            String uploadedByName, Long learningItemId) {
        this(id, title, fileName, contentType, fileSize, fileUrl, 
             description, uploadedAt, uploadedById, uploadedByName, 
             learningItemId, contentType != null && contentType.startsWith("video/"));
    }
} 