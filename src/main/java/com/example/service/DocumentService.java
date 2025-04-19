package com.example.service;

import com.example.dto.DocumentDto;
import com.example.dto.DocumentUploadDto;
import com.example.model.Document;
import com.example.model.LearningItem;
import com.example.model.User;
import com.example.repository.DocumentRepository;
import com.example.repository.LearningItemRepository;
import com.example.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LearningItemRepository learningItemRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Upload a new document
     * 
     * @param file The file to upload
     * @param uploadDto Metadata for the document
     * @param username Username of the uploader
     * @return The uploaded document DTO
     */
    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, DocumentUploadDto uploadDto, String username) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        
        LearningItem learningItem = null;
        if (uploadDto.learningItemId() != null) {
            learningItem = learningItemRepository.findById(uploadDto.learningItemId())
                    .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + uploadDto.learningItemId()));
        }
        
        // Upload file to storage
        String directory = learningItem != null ? "courses/" + learningItem.getWeek().getCourse().getId() : "documents";
        String fileUrl = fileStorageService.uploadFile(file, directory);
        
        // Determine if this is a video file
        boolean isVideo = false;
        if (uploadDto.isVideo() != null) {
            isVideo = uploadDto.isVideo();
        } else {
            String contentType = file.getContentType();
            isVideo = contentType != null && 
                     (contentType.startsWith("video/") || 
                      "application/x-mpegURL".equals(contentType) ||
                      "application/vnd.apple.mpegURL".equals(contentType));
        }
        
        // Create document entity
        Document document = Document.builder()
                .title(uploadDto.title())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(fileUrl)
                .description(uploadDto.description())
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(user)
                .learningItem(learningItem)
                .isVideo(isVideo)
                .build();
        
        Document savedDocument = documentRepository.save(document);
        
        return mapToDocumentDto(savedDocument);
    }
    
    /**
     * Get a document by ID
     * 
     * @param id Document ID
     * @return The document DTO
     */
    public DocumentDto getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        
        return mapToDocumentDto(document);
    }
    
    /**
     * Get all documents uploaded by a user
     * 
     * @param username Username of the uploader
     * @return List of document DTOs
     */
    public List<DocumentDto> getDocumentsByUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        
        List<Document> documents = documentRepository.findByUploadedBy(user);
        return documents.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all documents for a specific learning item
     * 
     * @param learningItemId Learning item ID
     * @return List of document DTOs
     */
    public List<DocumentDto> getDocumentsByLearningItem(Long learningItemId) {
        LearningItem learningItem = learningItemRepository.findById(learningItemId)
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + learningItemId));
        
        List<Document> documents = documentRepository.findByLearningItem(learningItem);
        return documents.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all standalone documents (not associated with a learning item)
     * 
     * @return List of document DTOs
     */
    public List<DocumentDto> getStandaloneDocuments() {
        List<Document> documents = documentRepository.findByLearningItemIsNull();
        return documents.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all video documents
     * 
     * @return List of document DTOs representing videos
     */
    public List<DocumentDto> getAllVideos() {
        List<Document> videos = documentRepository.findByIsVideoTrue();
        return videos.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all video documents for a specific learning item
     * 
     * @param learningItemId Learning item ID
     * @return List of document DTOs representing videos
     */
    public List<DocumentDto> getVideosByLearningItem(Long learningItemId) {
        LearningItem learningItem = learningItemRepository.findById(learningItemId)
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + learningItemId));
        
        List<Document> videos = documentRepository.findByLearningItemAndIsVideoTrue(learningItem);
        return videos.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search documents by keyword
     * 
     * @param keyword Search keyword
     * @return List of document DTOs
     */
    public List<DocumentDto> searchDocuments(String keyword) {
        List<Document> documents = documentRepository.searchDocuments(keyword);
        return documents.stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a document
     * 
     * @param id Document ID
     * @param username Username requesting the deletion (for authorization)
     * @return true if deletion was successful
     */
    @Transactional
    public boolean deleteDocument(Long id, String username) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        
        // Check if the user is the owner of the document
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        
        // Only the document uploader can delete it
        if (!document.getUploadedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this document");
        }
        
        // Delete the file from storage
        boolean fileDeleted = fileStorageService.deleteFile(document.getFileUrl());
        if (!fileDeleted) {
            return false;
        }
        
        // Delete the document record
        documentRepository.delete(document);
        return true;
    }
    
    /**
     * Update document metadata
     * 
     * @param id Document ID
     * @param uploadDto Updated metadata
     * @param username Username requesting the update (for authorization)
     * @return The updated document DTO
     */
    @Transactional
    public DocumentDto updateDocument(Long id, DocumentUploadDto uploadDto, String username) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        
        // Check if the user is the owner of the document
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        
        // Only the document uploader can update it
        if (!document.getUploadedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this document");
        }
        
        // Update document fields if provided
        if (uploadDto.title() != null) {
            document.setTitle(uploadDto.title());
        }
        
        if (uploadDto.description() != null) {
            document.setDescription(uploadDto.description());
        }
        
        if (uploadDto.isVideo() != null) {
            document.setIsVideo(uploadDto.isVideo());
        }
        
        // Update learning item if provided
        if (uploadDto.learningItemId() != null) {
            LearningItem learningItem = learningItemRepository.findById(uploadDto.learningItemId())
                    .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + uploadDto.learningItemId()));
            
            document.setLearningItem(learningItem);
        } else if (uploadDto.learningItemId() == null && uploadDto.title() == null) {
            // Special case: if learningItemId is explicitly null and title is not provided,
            // this indicates we want to disassociate the document from any learning item
            document.setLearningItem(null);
        }
        
        Document updatedDocument = documentRepository.save(document);
        return mapToDocumentDto(updatedDocument);
    }
    
    private DocumentDto mapToDocumentDto(Document document) {
        return new DocumentDto(
                document.getId(),
                document.getTitle(),
                document.getFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getFileUrl(),
                document.getDescription(),
                document.getUploadedAt(),
                document.getUploadedBy().getId(),
                document.getUploadedBy().getName(),
                document.getLearningItem() != null ? document.getLearningItem().getId() : null,
                document.getIsVideo()
        );
    }
} 