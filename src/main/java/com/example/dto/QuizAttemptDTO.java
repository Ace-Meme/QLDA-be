package com.example.dto;

import com.example.model.QuizAttemptStatus;
import java.time.LocalDateTime;

public record QuizAttemptDTO(
    Long id,
    Long studentId,
    String studentName,
    Long quizBankId,
    String quizBankTitle,
    Long learningItemId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer totalScore,
    Integer maxPossibleScore,
    QuizAttemptStatus status
) {
    public QuizAttemptDTO {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID must be specified");
        }
        if (quizBankId == null) {
            throw new IllegalArgumentException("Quiz bank ID must be specified");
        }
        if (learningItemId == null) {
            throw new IllegalArgumentException("Learning item ID must be specified");
        }
        if (status == null) {
            status = QuizAttemptStatus.IN_PROGRESS;
        }
    }
} 