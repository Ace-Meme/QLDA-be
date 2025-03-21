package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class SupabaseFileStorageServiceImpl implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(SupabaseFileStorageServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID() + "-" + fileName;
        
        // Create the full path based on the directory
        String path = directory != null && !directory.isEmpty() 
                ? directory + "/" + uniqueFileName 
                : uniqueFileName;
                
        // Create URL for upload
        String uploadUrl = supabaseUrl + "/" + bucket + "/" + path;
        
        logger.info("Uploading file to: {}", uploadUrl);
        
        // Set headers for Supabase authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", file.getContentType());
        headers.set("x-upsert", "true");  // Overwrite if exists
        
        // Create request entity
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        
        try {
            // Upload file
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                // Return the public URL
                logger.info("File uploaded successfully: {}", uploadUrl);
                return uploadUrl;
            } else {
                String errorMsg = "Failed to upload file to Supabase: " + response.getBody();
                logger.error(errorMsg);
                throw new IOException(errorMsg);
            }
        } catch (Exception e) {
            logger.error("Error uploading file to Supabase", e);
            throw new IOException("Error uploading file to Supabase: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        // Extract path from URL
        String path = fileUrl.replace(supabaseUrl + "/" + bucket + "/", "");
        
        // Create URL for deletion
        String deleteUrl = supabaseUrl + "/" + bucket + "/" + path;
        
        logger.info("Deleting file from: {}", deleteUrl);
        
        // Set headers for Supabase authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        
        // Create request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        try {
            // Delete file
            ResponseEntity<String> response = restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );
            
            boolean success = response.getStatusCode() == HttpStatus.OK || 
                   response.getStatusCode() == HttpStatus.NO_CONTENT;
                   
            if (success) {
                logger.info("File deleted successfully: {}", deleteUrl);
            } else {
                logger.warn("Failed to delete file: {}", response.getBody());
            }
            
            return success;
        } catch (Exception e) {
            logger.error("Error deleting file from Supabase", e);
            return false;
        }
    }
} 