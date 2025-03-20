package com.example.service;

import com.example.dto.DocumentDto;
import com.example.dto.DocumentUploadDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    
    /**
     * Upload a new document
     * 
     * @param file The file to upload
     * @param uploadDto Metadata for the document
     * @param username Username of the uploader
     * @return The uploaded document DTO
     */
    DocumentDto uploadDocument(MultipartFile file, DocumentUploadDto uploadDto, String username) throws IOException;
    
    /**
     * Get a document by ID
     * 
     * @param id Document ID
     * @return The document DTO
     */
    DocumentDto getDocumentById(Long id);
    
    /**
     * Get all documents uploaded by a user
     * 
     * @param username Username of the uploader
     * @return List of document DTOs
     */
    List<DocumentDto> getDocumentsByUser(String username);
    
    /**
     * Get all documents for a specific learning item
     * 
     * @param learningItemId Learning item ID
     * @return List of document DTOs
     */
    List<DocumentDto> getDocumentsByLearningItem(Long learningItemId);
    
    /**
     * Get all standalone documents (not associated with a learning item)
     * 
     * @return List of document DTOs
     */
    List<DocumentDto> getStandaloneDocuments();
    
    /**
     * Search documents by keyword
     * 
     * @param keyword Search keyword
     * @return List of document DTOs
     */
    List<DocumentDto> searchDocuments(String keyword);
    
    /**
     * Delete a document
     * 
     * @param id Document ID
     * @param username Username requesting the deletion (for authorization)
     * @return true if deletion was successful
     */
    boolean deleteDocument(Long id, String username);
    
    /**
     * Update document metadata
     * 
     * @param id Document ID
     * @param uploadDto Updated metadata
     * @param username Username requesting the update (for authorization)
     * @return The updated document DTO
     */
    DocumentDto updateDocument(Long id, DocumentUploadDto uploadDto, String username);
} 