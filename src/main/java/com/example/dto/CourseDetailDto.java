package com.example.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailDto {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private boolean isFree;
    private boolean isDraft;
    private Integer numberOfLessons;
    private Integer totalDurationMinutes;
    private Integer estimatedWeeks;
    private String summary;
    private String description;
    private String thumbnailUrl;
    private String teacherName;
    private List<WeekDto> weeks;
}
