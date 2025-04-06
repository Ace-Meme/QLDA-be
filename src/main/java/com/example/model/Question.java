package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "QUESTIONS")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_bank_id")
    private QuizBank quizBank;
    
    @Column(columnDefinition = "TEXT")
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    
    @Column(columnDefinition = "TEXT")
    private String options; // JSON array of options
    
    private String correctAnswer;
} 