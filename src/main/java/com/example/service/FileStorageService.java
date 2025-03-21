package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Uploads a file to the storage service
     * 
     * @param file The file to upload
     * @param directory The directory in the storage to place the file (optional)
     * @return The public URL of the uploaded file
     * @throws IOException If an error occurs during upload
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * Deletes a file from the storage service
     * 
     * @param fileUrl The URL of the file to delete
     * @return true if deletion is successful, false otherwise
     */
    boolean deleteFile(String fileUrl);
} 