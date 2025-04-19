package com.example.dto;

import com.example.model.QuestionType;
import java.util.List;

// DTO for creating a new question, excluding the ID
public record QuestionCreateDTO(
    Long quizBankId,
    String questionText,
    QuestionType questionType,
    List<String> options,
    String correctAnswer
) {
    // Basic validation can be kept or enhanced as needed
    public QuestionCreateDTO {
        if (quizBankId == null) {
            throw new IllegalArgumentException("Quiz bank ID must be specified");
        }
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