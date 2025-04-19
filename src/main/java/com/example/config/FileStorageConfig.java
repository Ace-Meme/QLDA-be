package com.example.config;

import com.example.service.FileStorageService;
import com.example.service.LocalFileStorageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FileStorageConfig {

    @Bean
    @Primary
    public FileStorageService fileStorageService() {
        // Use local file storage instead of Supabase
        return new LocalFileStorageServiceImpl();
    }
} 