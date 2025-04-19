package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This is only needed if you want to serve static resources through Spring MVC
        // In our case, we're using a dedicated controller for file access, so this isn't strictly necessary
        
        // Register resource handler for uploaded files if needed for direct access
        Path uploadPath = Paths.get(uploadDir);
        String uploadPathStr = uploadPath.toFile().getAbsolutePath();
        
        // Map /uploads/** to the file system location
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPathStr + "/");
    }
} 