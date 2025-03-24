package com.example.service;

import com.example.dto.WeekCreateDto;
import com.example.dto.WeekDto;
import com.example.dto.WeekUpdateDto;

import java.util.List;

public interface WeekService {
    
    /**
     * Get all weeks for a course
     * 
     * @param courseId The ID of the course
     * @return List of week DTOs
     */
    List<WeekDto> getWeeksByCourseId(Long courseId);
    
    /**
     * Get a week by ID
     * 
     * @param weekId The ID of the week
     * @return Week DTO
     */
    WeekDto getWeekById(Long weekId);
    
    /**
     * Create a new week
     * 
     * @param weekCreateDto DTO containing week data
     * @return Created week DTO
     */
    WeekDto createWeek(WeekCreateDto weekCreateDto);
    
    /**
     * Update an existing week
     * 
     * @param weekId The ID of the week to update
     * @param weekUpdateDto DTO containing updated week data
     * @return Updated week DTO
     */
    WeekDto updateWeek(Long weekId, WeekUpdateDto weekUpdateDto);
    
    /**
     * Delete a week
     * 
     * @param weekId The ID of the week to delete
     */
    void deleteWeek(Long weekId);
} 