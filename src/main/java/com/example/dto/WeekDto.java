package com.example.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekDto {
    private Long id;
    private String title;
    private String description;
    private Integer weekNumber;
    private List<LearningItemDto> learningItems;
}