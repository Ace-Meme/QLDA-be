package com.example.repository;

import com.example.model.Document;
import com.example.model.User;
import com.example.model.LearningItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedBy(User user);
    
    List<Document> findByLearningItem(LearningItem learningItem);
    
    List<Document> findByLearningItemIsNull();
    
    @Query("SELECT d FROM Document d WHERE d.learningItem.week.course.id = :courseId")
    List<Document> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT d FROM Document d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchDocuments(@Param("keyword") String keyword);
} 