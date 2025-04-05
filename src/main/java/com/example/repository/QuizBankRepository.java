package com.example.repository;

import com.example.model.QuizBank;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizBankRepository extends JpaRepository<QuizBank, Long> {
    
    List<QuizBank> findByCreatedByAndActiveIsTrue(User teacher);
    
    @Query("SELECT qb FROM QuizBank qb WHERE qb.active = true")
    List<QuizBank> findAllActive();
    
    @Query("SELECT qb FROM QuizBank qb WHERE qb.id = :id AND qb.active = true")
    QuizBank findActiveById(Long id);
} 