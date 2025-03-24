package com.example.repository;

import com.example.model.LearningItem;
import com.example.model.LearningItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningItemRepository extends JpaRepository<LearningItem, Long> {
    
    List<LearningItem> findByWeekIdOrderByOrderIndex(Long weekId);
    
    List<LearningItem> findByWeekIdAndTypeOrderByOrderIndex(Long weekId, LearningItemType type);
    
    long countByWeekId(Long weekId);
}