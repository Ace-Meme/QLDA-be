package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuizResultDTO(
    Long quizAttemptId,
    String quizTitle,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer totalScore,
    Integer maxPossibleScore,
    Double percentageScore,
    List<StudentResponseDTO> responses
) {
    public QuizResultDTO {
        if (quizAttemptId == null) {
            throw new IllegalArgumentException("Quiz attempt ID must be specified");
        }
        if (responses == null) {
            throw new IllegalArgumentException("Responses must be specified");
        }
        if (percentageScore == null && totalScore != null && maxPossibleScore != null && maxPossibleScore > 0) {
            percentageScore = (double) totalScore / maxPossibleScore * 100;
        }
    }
} 