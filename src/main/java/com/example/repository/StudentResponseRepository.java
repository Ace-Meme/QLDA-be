package com.example.repository;

import com.example.model.StudentResponse;
import com.example.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, Long> {
    
    List<StudentResponse> findByQuizAttempt(QuizAttempt quizAttempt);
    
    @Query("SELECT sr FROM StudentResponse sr WHERE sr.quizAttempt.id = :quizAttemptId ORDER BY sr.id")
    List<StudentResponse> findByQuizAttemptId(Long quizAttemptId);
    
    @Query("SELECT COUNT(sr) FROM StudentResponse sr WHERE sr.quizAttempt.id = :quizAttemptId AND sr.isCorrect = true")
    Long countCorrectAnswersByQuizAttemptId(Long quizAttemptId);
    
    @Query("SELECT SUM(sr.pointsEarned) FROM StudentResponse sr WHERE sr.quizAttempt.id = :quizAttemptId")
    Integer sumPointsEarnedByQuizAttemptId(Long quizAttemptId);
} 