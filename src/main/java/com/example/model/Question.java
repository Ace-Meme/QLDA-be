package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bankId;

    private String createdByLecturerUsername;

    private String content;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    private Boolean showAnswer = false;
}
