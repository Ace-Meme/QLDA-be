package com.example.controller;

import com.example.dto.CourseCreateDto;
import com.example.dto.CourseDetailDto;
import com.example.dto.CourseDto;
import com.example.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Course Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(
        summary = "Get all published courses",
        description = "Retrieves a list of all published courses with summary information"
    )
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllPublishedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    @Operation(
        summary = "Get course details",
        description = "Retrieves detailed information about a specific course, including weeks and learning items"
    )
    public ResponseEntity<CourseDetailDto> getCourseById(@PathVariable Long courseId) {
        CourseDetailDto course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @Operation(
        summary = "Create a new course",
        description = "Creates a new course with the provided information"
    )
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseCreateDto courseCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        CourseDto createdCourse = courseService.createCourse(courseCreateDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PutMapping("/{courseId}")
    @Operation(
        summary = "Update a course",
        description = "Updates an existing course with the provided information"
    )
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseCreateDto courseUpdateDto) {
        CourseDto updatedCourse = courseService.updateCourse(courseId, courseUpdateDto);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{courseId}")
    @Operation(
        summary = "Delete a course",
        description = "Deletes a course with the specified ID"
    )
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}