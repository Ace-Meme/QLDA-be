package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageServiceImpl.class);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Uploads a file to the local file system
     * 
     * @param file The file to upload
     * @param directory The subdirectory in uploads to place the file
     * @return The relative URL path to access the file
     * @throws IOException If an error occurs during upload
     */
    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file");
        }
        
        // Create a unique file name to prevent collisions
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex > 0) {
            fileExtension = originalFilename.substring(extensionIndex);
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Create the target directory
        String targetDir = uploadDir;
        if (directory != null && !directory.isEmpty()) {
            targetDir = uploadDir + "/" + directory;
        }
        
        Path targetLocation = Paths.get(targetDir).toAbsolutePath().normalize();
        
        // Create directories if they don't exist
        if (!Files.exists(targetLocation)) {
            try {
                Files.createDirectories(targetLocation);
            } catch (IOException ex) {
                throw new IOException("Failed to create directory: " + targetLocation, ex);
            }
        }
        
        // Save the file to the target location
        Path filePath = targetLocation.resolve(uniqueFilename);
        
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved successfully: {}", filePath);
            
            // Return the relative URL to access the file
            String relativePath = directory != null && !directory.isEmpty() 
                    ? directory + "/" + uniqueFilename 
                    : uniqueFilename;
                    
            return "/files/" + relativePath;
        } catch (IOException ex) {
            throw new IOException("Failed to store file " + uniqueFilename, ex);
        }
    }

    /**
     * Deletes a file from the local file system
     * 
     * @param fileUrl The URL of the file to delete
     * @return true if deletion is successful, false otherwise
     */
    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        // Extract the relative path from the URL
        String relativePath = fileUrl;
        if (fileUrl.startsWith("/files/")) {
            relativePath = fileUrl.substring("/files/".length());
        }
        
        Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
        
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Error deleting file: {}", e.getMessage());
            return false;
        }
    }
} 