package com.example.dto;

public record StudentResponseDTO(
    Long id,
    Long quizAttemptId,
    Long questionId,
    String questionText,
    String selectedAnswer,
    String correctAnswer,
    Boolean isCorrect,
    Integer pointsEarned
) {
    public StudentResponseDTO {
        if (quizAttemptId == null) {
            throw new IllegalArgumentException("Quiz attempt ID must be specified");
        }
        if (questionId == null) {
            throw new IllegalArgumentException("Question ID must be specified");
        }
    }
} 