package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "QUIZ_ATTEMPTS")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_bank_id")
    private QuizBank quizBank;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_item_id")
    private LearningItem learningItem;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer totalScore;
    
    private Integer maxPossibleScore;
    
    @Enumerated(EnumType.STRING)
    private QuizAttemptStatus status;
} 