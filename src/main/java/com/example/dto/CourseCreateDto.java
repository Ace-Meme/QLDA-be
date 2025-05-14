package com.example.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateDto {
    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must be less than 255 characters")
    private String name;
    
    private String category;
    private BigDecimal price;
    private boolean isFree;
    private boolean isDraft;

    private String userName;
    
    @Size(max = 500, message = "Summary must be less than 500 characters")
    private String summary;
    private String description;
    private String thumbnailUrl;
}
