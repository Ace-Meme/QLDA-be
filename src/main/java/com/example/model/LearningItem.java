package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LEARNING_ITEMS")
public class LearningItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Enumerated(EnumType.STRING)
    private LearningItemType type;
    
    private String content;
    
    private Integer durationMinutes;
    
    private Integer orderIndex;
    
    @ManyToOne
    @JoinColumn(name = "week_id")
    private Week week;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_bank_id")
    private QuizBank quizBank;
}