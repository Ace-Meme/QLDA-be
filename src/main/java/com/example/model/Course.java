package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COURSES")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String category;
    
    private BigDecimal price;
    
    private boolean isFree;
    
    private boolean isDraft;
    
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String thumbnailUrl;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Week> weeks = new ArrayList<>();
    
    @ManyToMany(mappedBy = "enrolledCourses", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> enrolledStudents = new HashSet<>();
    
    public Integer getEstimatedWeeks() {
        return weeks != null ? weeks.size() : 0;
    }
}