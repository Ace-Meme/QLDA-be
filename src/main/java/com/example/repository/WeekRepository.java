package com.example.repository;

import com.example.model.Week;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeekRepository extends JpaRepository<Week, Long> {
    
    List<Week> findByCourseIdOrderByWeekNumber(Long courseId);
}