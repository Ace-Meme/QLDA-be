package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/files")
@Tag(name = "File Service", description = "APIs for serving uploaded files")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    /**
     * Serve an uploaded file using the full path after /files/
     * This handles any nested directories
     */
    @Operation(
        summary = "Download a file by path",
        description = "Download a file using its full path after /files/"
    )
    @GetMapping("/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        try {
            // Extract the path after "/files/"
            String requestPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String filePath = requestPath.substring("/files/".length());
            
            logger.info("Requested file path: {}", filePath);
            
            // Resolve the file path relative to the upload directory
            Path path = Paths.get(uploadDir).resolve(filePath).normalize();
            logger.info("Resolved absolute path: {}", path);
            
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(path);
                boolean isAttachment = !isStreamableMediaType(contentType);
                
                HttpHeaders headers = new HttpHeaders();
                if (isAttachment) {
                    // For documents, send as attachment for download
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
                } else {
                    // For media files like videos, enable streaming
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");
                    // Add range request headers for video streaming
                    headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
                }
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                logger.error("File not found: {}", path);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Malformed URL error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error serving file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Determine content type based on file extension or probing
     */
    private String determineContentType(Path filePath) {
        try {
            return Files.probeContentType(filePath);
        } catch (IOException e) {
            // Fall back to extension-based content type detection
            String fileName = filePath.getFileName().toString();
            if (fileName.endsWith(".pdf")) {
                return "application/pdf";
            } else if (fileName.endsWith(".mp4")) {
                return "video/mp4";
            } else if (fileName.endsWith(".mov")) {
                return "video/quicktime";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".docx")) {
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (fileName.endsWith(".xlsx")) {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (fileName.endsWith(".pptx")) {
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            } else {
                return "application/octet-stream";
            }
        }
    }
    
    /**
     * Check if the content type is a streamable media type (video, audio)
     */
    private boolean isStreamableMediaType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        List<String> streamableTypes = new ArrayList<>();
        // Video types
        streamableTypes.add("video/mp4");
        streamableTypes.add("video/webm");
        streamableTypes.add("video/ogg");
        streamableTypes.add("video/quicktime");
        streamableTypes.add("application/x-mpegURL");
        streamableTypes.add("application/vnd.apple.mpegURL");
        // Audio types
        streamableTypes.add("audio/mpeg");
        streamableTypes.add("audio/ogg");
        streamableTypes.add("audio/wav");
        
        return streamableTypes.contains(contentType) || contentType.startsWith("video/") || contentType.startsWith("audio/");
    }
} 