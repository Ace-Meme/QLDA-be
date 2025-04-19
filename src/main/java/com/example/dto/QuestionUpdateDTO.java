package com.example.dto;

import com.example.model.QuestionType;
import java.util.List;

// DTO for updating an existing question, excluding ID and quizBankId
public record QuestionUpdateDTO(
    String questionText,
    QuestionType questionType,
    List<String> options,
    String correctAnswer
) {
    // Basic validation
    public QuestionUpdateDTO {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be blank");
        }
        if (questionType == null) {
            throw new IllegalArgumentException("Question type must be specified");
        }
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Question must have at least one option");
        }
        if (correctAnswer == null || correctAnswer.isBlank()) {
            throw new IllegalArgumentException("Correct answer must be specified");
        }
    }
} 