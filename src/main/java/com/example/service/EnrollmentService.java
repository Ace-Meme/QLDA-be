package com.example.service;

import com.example.dto.CourseDto;
import com.example.dto.UserDto;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void enrollStudentInCourse(Long courseId, String username) {
        User user = userRepository.findWithEnrolledCoursesByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        if (course.isDraft()) {
            throw new IllegalArgumentException("Cannot enroll in a draft course.");
        }

        if (user.getEnrolledCourses().contains(course)) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        user.getEnrolledCourses().add(course);
        userRepository.save(user); // Persist changes
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getEnrolledCourses(String username) {
        User user = userRepository.findWithEnrolledCoursesByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        Set<Course> enrolledCourses = user.getEnrolledCourses();
        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return Collections.emptyList();
        }

        return enrolledCourses.stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
    }

    // Using Builder pattern and matching CourseDto fields
    private CourseDto mapToCourseDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .category(course.getCategory())
                .price(course.getPrice())
                .isFree(course.isFree())
                .isDraft(course.isDraft())
                .estimatedWeeks(course.getEstimatedWeeks()) // Added estimatedWeeks
                .summary(course.getSummary())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .teacherName(course.getTeacher() != null ? course.getTeacher().getName() : null) 
                .build();
    }

    // This helper method might be better placed in a dedicated UserMapper
    private UserDto mapToUserDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getUsername());
    }
} 