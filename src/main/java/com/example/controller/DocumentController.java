package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.DocumentDto;
import com.example.dto.DocumentUploadDto;
import com.example.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "APIs for managing document resources")
@Hidden
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Operation(
        summary = "Upload a document",
        description = "Upload a new document with optional association to a learning item"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentDto>> uploadDocument(
            @Parameter(description = "Document file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Document title") @RequestParam("title") String title,
            @Parameter(description = "Document description (optional)") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Learning item ID to associate with (optional)") @RequestParam(value = "learningItemId", required = false) Long learningItemId,
            Authentication authentication) {
        
        try {
            DocumentUploadDto uploadDto = new DocumentUploadDto(title, description, learningItemId);
            DocumentDto documentDto = documentService.uploadDocument(file, uploadDto, authentication.getName());
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document uploaded successfully", documentDto), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to upload document: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Unexpected error: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get document by ID",
        description = "Retrieve a document by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentDto>> getDocumentById(
            @Parameter(description = "ID of the document") @PathVariable Long id) {
        try {
            DocumentDto documentDto = documentService.getDocumentById(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document retrieved successfully", documentDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get current user documents",
        description = "Retrieve all documents uploaded by the authenticated user"
    )
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<DocumentDto>>> getUserDocuments(Authentication authentication) {
        try {
            List<DocumentDto> documents = documentService.getDocumentsByUser(authentication.getName());
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "User documents retrieved successfully", documents), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get documents by learning item",
        description = "Retrieve all documents associated with a specific learning item"
    )
    @GetMapping("/learning-item/{learningItemId}")
    public ResponseEntity<ApiResponse<List<DocumentDto>>> getDocumentsByLearningItem(
            @Parameter(description = "ID of the learning item") @PathVariable Long learningItemId) {
        try {
            List<DocumentDto> documents = documentService.getDocumentsByLearningItem(learningItemId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning item documents retrieved successfully", documents), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get standalone documents",
        description = "Retrieve all documents that are not associated with any learning item"
    )
    @GetMapping("/standalone")
    public ResponseEntity<ApiResponse<List<DocumentDto>>> getStandaloneDocuments() {
        try {
            List<DocumentDto> documents = documentService.getStandaloneDocuments();
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Standalone documents retrieved successfully", documents), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Search documents",
        description = "Search for documents by keyword in title or description"
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DocumentDto>>> searchDocuments(
            @Parameter(description = "Keyword to search in document title and description") @RequestParam String keyword) {
        try {
            List<DocumentDto> documents = documentService.searchDocuments(keyword);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Search completed successfully", documents), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Delete document",
        description = "Delete a document by its ID (only owner can delete)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteDocument(
            @Parameter(description = "ID of the document to delete") @PathVariable Long id, 
            Authentication authentication) {
        try {
            boolean deleted = documentService.deleteDocument(id, authentication.getName());
            if (deleted) {
                return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document deleted successfully", true), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to delete document", false), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Update document",
        description = "Update document title, description or learning item association"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentDto>> updateDocument(
            @Parameter(description = "ID of the document to update") @PathVariable Long id,
            @RequestBody @Valid DocumentUploadDto uploadDto,
            Authentication authentication) {
        try {
            DocumentDto updatedDocument = documentService.updateDocument(id, uploadDto, authentication.getName());
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document updated successfully", updatedDocument), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(
        summary = "Associate document with learning item",
        description = "Associate an existing document with a specific learning item"
    )
    @PutMapping("/{id}/associate/{learningItemId}")
    public ResponseEntity<ApiResponse<DocumentDto>> associateWithLearningItem(
            @Parameter(description = "ID of the document") @PathVariable Long id,
            @Parameter(description = "ID of the learning item to associate with") @PathVariable Long learningItemId,
            Authentication authentication) {
        try {
            DocumentUploadDto updateDto = new DocumentUploadDto(null, null, learningItemId);
            DocumentDto updatedDocument = documentService.updateDocument(id, updateDto, authentication.getName());
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document associated with learning item successfully", updatedDocument), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(
        summary = "Disassociate document from learning item",
        description = "Remove the association between a document and its learning item"
    )
    @PutMapping("/{id}/disassociate")
    public ResponseEntity<ApiResponse<DocumentDto>> disassociateFromLearningItem(
            @Parameter(description = "ID of the document") @PathVariable Long id,
            Authentication authentication) {
        try {
            DocumentUploadDto updateDto = new DocumentUploadDto(null, null, null);
            DocumentDto updatedDocument = documentService.updateDocument(id, updateDto, authentication.getName());
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Document disassociated from learning item successfully", updatedDocument), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 