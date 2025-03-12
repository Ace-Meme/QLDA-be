package com.example.repository;

import com.example.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByIsDraftFalse();
    
    @Query("SELECT c FROM Course c WHERE c.isDraft = false AND c.teacher.id = :teacherId")
    List<Course> findPublishedCoursesByTeacher(Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId")
    List<Course> findCoursesByTeacher(Long teacherId);
    
    @Query(value = "SELECT c.* FROM courses c JOIN users u ON c.teacher_id = u.id " +
           "WHERE c.is_draft = false " +
           "AND (CAST(:name AS text) IS NULL OR c.name ILIKE CONCAT('%', CAST(:name AS text), '%')) " +
           "AND (CAST(:teacherName AS text) IS NULL OR u.name ILIKE CONCAT('%', CAST(:teacherName AS text), '%'))",
           nativeQuery = true)
    Page<Course> findPublishedCoursesByNameAndTeacher(
            @Param("name") String name, 
            @Param("teacherName") String teacherName, 
            Pageable pageable);
}