package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.DocumentDto;
import com.example.service.DocumentService;
import com.example.service.LearningItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "APIs for managing documents and videos")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private LearningItemService learningItemService;
    
    @Operation(
        summary = "Get document by ID",
        description = "Retrieve a document by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentDto>> getDocumentById(
            @Parameter(description = "ID of the document") @PathVariable Long id) {
        
        try {
            DocumentDto document = documentService.getDocumentById(id);
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "Document retrieved successfully", document),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        }
    }
    
    @Operation(
        summary = "Download document by ID",
        description = "Download a document directly by its ID"
    )
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDocument(
            @Parameter(description = "ID of the document") @PathVariable Long id) {
        
        try {
            DocumentDto document = documentService.getDocumentById(id);
            
            // Redirect to the file URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, document.fileUrl())
                    .build();
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        }
    }
    
    @Operation(
        summary = "Download document by learning item ID",
        description = "Download a document associated with a specific learning item"
    )
    @GetMapping("/learning-item/{learningItemId}/download")
    public ResponseEntity<?> downloadDocumentByLearningItem(
            @Parameter(description = "ID of the learning item") @PathVariable Long learningItemId) {
        
        try {
            // Get the document associated with this learning item
            List<DocumentDto> documents = documentService.getDocumentsByLearningItem(learningItemId);
            
            if (documents.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>("ERROR", "No document found for this learning item", null),
                        HttpStatus.NOT_FOUND);
            }
            
            // Get the document (there should only be one)
            DocumentDto document = documents.get(0);
            
            // Redirect to the file URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, document.fileUrl())
                    .build();
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 