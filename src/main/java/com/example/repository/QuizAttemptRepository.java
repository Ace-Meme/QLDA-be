package com.example.repository;

import com.example.model.QuizAttempt;
import com.example.model.User;
import com.example.model.LearningItem;
import com.example.model.QuizAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    List<QuizAttempt> findByStudent(User student);
    
    List<QuizAttempt> findByStudentAndStatus(User student, QuizAttemptStatus status);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.student.id = :studentId AND qa.learningItem.id = :learningItemId ORDER BY qa.startTime DESC")
    List<QuizAttempt> findByStudentIdAndLearningItemId(Long studentId, Long learningItemId);
    
    Optional<QuizAttempt> findByStudentAndLearningItemAndStatus(User student, LearningItem learningItem, QuizAttemptStatus status);
} 