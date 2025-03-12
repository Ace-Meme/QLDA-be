package com.example.dto;

import com.example.model.LearningItemType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningItemDto {
    private Long id;
    private String title;
    private LearningItemType type;
    private String content;
    private Integer durationMinutes;
    private Integer orderIndex;
}