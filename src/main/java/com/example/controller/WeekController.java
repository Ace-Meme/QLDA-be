package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.WeekCreateDto;
import com.example.dto.WeekDto;
import com.example.dto.WeekUpdateDto;
import com.example.service.WeekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Week Management", description = "APIs for managing weeks")
@RestController
@RequiredArgsConstructor
@RequestMapping("/weeks")
public class WeekController {

    private final WeekService weekService;

    @GetMapping("/course/{courseId}")
    @Operation(
        summary = "Get all weeks for a course",
        description = "Retrieves a list of all weeks for a specific course ordered by week number"
    )
    public ResponseEntity<ApiResponse<List<WeekDto>>> getWeeksByCourseId(
            @Parameter(description = "ID of the course") 
            @PathVariable Long courseId) {
        try {
            List<WeekDto> weeks = weekService.getWeeksByCourseId(courseId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Weeks retrieved successfully", weeks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/{weekId}")
    @Operation(
        summary = "Get week by ID",
        description = "Retrieves details of a specific week by its ID"
    )
    public ResponseEntity<ApiResponse<WeekDto>> getWeekById(
            @Parameter(description = "ID of the week") 
            @PathVariable Long weekId) {
        try {
            WeekDto week = weekService.getWeekById(weekId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Week retrieved successfully", week));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PostMapping
    @Operation(
        summary = "Create a new week",
        description = "Creates a new week for a course with the provided information"
    )
    public ResponseEntity<ApiResponse<WeekDto>> createWeek(
            @Valid @RequestBody WeekCreateDto weekCreateDto) {
        try {
            WeekDto createdWeek = weekService.createWeek(weekCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("SUCCESS", "Week created successfully", createdWeek));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PutMapping("/{weekId}")
    @Operation(
        summary = "Update a week",
        description = "Updates an existing week with the provided information"
    )
    public ResponseEntity<ApiResponse<WeekDto>> updateWeek(
            @Parameter(description = "ID of the week to update") 
            @PathVariable Long weekId,
            @Valid @RequestBody WeekUpdateDto weekUpdateDto) {
        try {
            WeekDto updatedWeek = weekService.updateWeek(weekId, weekUpdateDto);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Week updated successfully", updatedWeek));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{weekId}")
    @Operation(
        summary = "Delete a week",
        description = "Deletes a week with the specified ID"
    )
    public ResponseEntity<ApiResponse<Void>> deleteWeek(
            @Parameter(description = "ID of the week to delete") 
            @PathVariable Long weekId) {
        try {
            weekService.deleteWeek(weekId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Week deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 