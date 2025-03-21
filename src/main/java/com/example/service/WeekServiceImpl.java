package com.example.service;

import com.example.dto.LearningItemDto;
import com.example.dto.WeekCreateDto;
import com.example.dto.WeekDto;
import com.example.dto.WeekUpdateDto;
import com.example.model.Course;
import com.example.model.LearningItem;
import com.example.model.Week;
import com.example.repository.CourseRepository;
import com.example.repository.LearningItemRepository;
import com.example.repository.WeekRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeekServiceImpl implements WeekService {

    private final WeekRepository weekRepository;
    private final CourseRepository courseRepository;
    private final LearningItemRepository learningItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WeekDto> getWeeksByCourseId(Long courseId) {
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
        
        List<Week> weeks = weekRepository.findByCourseIdOrderByWeekNumber(courseId);
        return weeks.stream()
                .map(this::mapToWeekDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WeekDto getWeekById(Long weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
        
        return mapToWeekDto(week);
    }

    @Override
    @Transactional
    public WeekDto createWeek(WeekCreateDto weekCreateDto) {
        Course course = courseRepository.findById(weekCreateDto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + weekCreateDto.getCourseId()));
        
        // Check if a week with the same number already exists in the course
        List<Week> existingWeeks = weekRepository.findByCourseIdOrderByWeekNumber(course.getId());
        boolean weekNumberExists = existingWeeks.stream()
                .anyMatch(w -> w.getWeekNumber().equals(weekCreateDto.getWeekNumber()));
        
        if (weekNumberExists) {
            throw new IllegalArgumentException("A week with number " + weekCreateDto.getWeekNumber() + 
                    " already exists in this course");
        }
        
        Week week = Week.builder()
                .title(weekCreateDto.getTitle())
                .description(weekCreateDto.getDescription())
                .weekNumber(weekCreateDto.getWeekNumber())
                .course(course)
                .build();
        
        Week savedWeek = weekRepository.save(week);
        return mapToWeekDto(savedWeek);
    }

    @Override
    @Transactional
    public WeekDto updateWeek(Long weekId, WeekUpdateDto weekUpdateDto) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
        
        // Check if updating week number and if it conflicts with existing weeks
        if (weekUpdateDto.getWeekNumber() != null && 
                !week.getWeekNumber().equals(weekUpdateDto.getWeekNumber())) {
            
            List<Week> weeksInCourse = weekRepository.findByCourseIdOrderByWeekNumber(week.getCourse().getId());
            boolean weekNumberExists = weeksInCourse.stream()
                    .filter(w -> !w.getId().equals(weekId)) // exclude current week
                    .anyMatch(w -> w.getWeekNumber().equals(weekUpdateDto.getWeekNumber()));
            
            if (weekNumberExists) {
                throw new IllegalArgumentException("A week with number " + weekUpdateDto.getWeekNumber() + 
                        " already exists in this course");
            }
            
            week.setWeekNumber(weekUpdateDto.getWeekNumber());
        }
        
        week.setTitle(weekUpdateDto.getTitle());
        week.setDescription(weekUpdateDto.getDescription());
        
        Week updatedWeek = weekRepository.save(week);
        return mapToWeekDto(updatedWeek);
    }

    @Override
    @Transactional
    public void deleteWeek(Long weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
        
        weekRepository.delete(week);
    }
    
    private WeekDto mapToWeekDto(Week week) {
        List<LearningItem> learningItems = learningItemRepository.findByWeekIdOrderByOrderIndex(week.getId());
        
        List<LearningItemDto> learningItemDtos = learningItems.stream()
                .map(item -> new LearningItemDto(
                        item.getId(),
                        item.getTitle(),
                        item.getType(),
                        item.getContent(),
                        item.getDurationMinutes(),
                        item.getOrderIndex(),
                        week.getId(),
                        week.getTitle(),
                        List.of()
                ))
                .collect(Collectors.toList());
        
        return WeekDto.builder()
                .id(week.getId())
                .title(week.getTitle())
                .description(week.getDescription())
                .weekNumber(week.getWeekNumber())
                .learningItems(learningItemDtos)
                .build();
    }
} 