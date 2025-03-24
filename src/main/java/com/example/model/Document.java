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
@Table(name = "DOCUMENTS")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    private String fileName;
    
    private String contentType;
    
    private Long fileSize;
    
    private String fileUrl;
    
    private String description;
    
    private LocalDateTime uploadedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
    
    // Optional relationship with learning item
    // When null, document exists independently
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_item_id")
    private LearningItem learningItem;
} 