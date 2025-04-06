package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.CourseDto;
import com.example.dto.EnrollmentRequestDto;
import com.example.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
@Tag(name = "Enrollment Management", description = "APIs for managing course enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in a course", description = "Enrolls the currently authenticated student in the specified course.")
    public ResponseEntity<ApiResponse<Void>> enrollInCourse(@Valid @RequestBody EnrollmentRequestDto enrollmentRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            enrollmentService.enrollStudentInCourse(enrollmentRequestDto.courseId(), username);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Successfully enrolled in the course.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my enrolled courses", description = "Retrieves the list of courses the currently authenticated student is enrolled in.")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getMyEnrolledCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            List<CourseDto> enrolledCourses = enrollmentService.getEnrolledCourses(username);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Enrolled courses retrieved successfully.", enrolledCourses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 