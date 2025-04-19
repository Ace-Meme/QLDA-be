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
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime uploadedAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploadedBy;
    
    @ManyToOne
    @JoinColumn(name = "learning_item_id")
    private LearningItem learningItem;
    
    /**
     * Indicates if this document is a video file
     * Used for UI rendering decisions
     */
    private Boolean isVideo;
    
    /**
     * Check if this document is a video based on its content type
     * @return true if content type indicates video
     */
    @Transient
    public boolean isVideoContent() {
        return contentType != null && 
               (contentType.startsWith("video/") || 
                "application/x-mpegURL".equals(contentType) ||
                "application/vnd.apple.mpegURL".equals(contentType));
    }
} 