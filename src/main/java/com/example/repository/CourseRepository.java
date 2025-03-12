package com.example.repository;

import com.example.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByIsDraftFalse();
    
    @Query("SELECT c FROM Course c WHERE c.isDraft = false AND c.teacher.id = :teacherId")
    List<Course> findPublishedCoursesByTeacher(Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId")
    List<Course> findCoursesByTeacher(Long teacherId);
}