package com.example.repository;

import com.example.model.LearningItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningItemRepository extends JpaRepository<LearningItem, Long> {
    
    List<LearningItem> findByWeekIdOrderByOrderIndex(Long weekId);
}