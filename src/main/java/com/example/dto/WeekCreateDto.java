package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekCreateDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @NotNull(message = "Week number is required")
    @Positive(message = "Week number must be positive")
    private Integer weekNumber;
    
    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be positive")
    private Long courseId;
} 