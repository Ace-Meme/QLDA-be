package com.example.repository;

import com.example.model.Question;
import com.example.model.QuizBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByQuizBank(QuizBank quizBank);
    
    @Query("SELECT q FROM Question q WHERE q.quizBank.id = :quizBankId ORDER BY q.id")
    List<Question> findByQuizBankId(Long quizBankId);
    
    @Query("SELECT q FROM Question q WHERE q.quizBank.id = :quizBankId ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByQuizBankId(Long quizBankId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.quizBank.id = :quizBankId")
    Long countByQuizBankId(Long quizBankId);
} 