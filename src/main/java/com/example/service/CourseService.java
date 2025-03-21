package com.example.service;

import com.example.dto.*;
import com.example.dto.CourseDetailDto;
import com.example.dto.CourseDto;
import com.example.dto.LearningItemDto;
import com.example.dto.WeekDto;
import com.example.model.Course;
import com.example.model.LearningItem;
import com.example.model.Week;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.LearningItemRepository;
import com.example.repository.UserRepository;
import com.example.repository.WeekRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final WeekRepository weekRepository;
    private final LearningItemRepository learningItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CourseDto> getAllPublishedCourses() {
        List<Course> courses = courseRepository.findByIsDraftFalse();
        return courses.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PagedResponseDto<CourseDto> getPublishedCourses(String name, String teacher, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        // Convert search parameters to lowercase if they're not null
        String searchName = name != null ? name.toLowerCase() : null;
        String searchTeacher = teacher != null ? teacher.toLowerCase() : null;
        
        Page<Course> coursePage = courseRepository.findPublishedCoursesByNameAndTeacher(searchName, searchTeacher, pageable);
        
        List<CourseDto> courseDtos = coursePage.getContent().stream()
                .map(this::mapToCourseDto)
                .collect(Collectors.toList());
        
        return PagedResponseDto.<CourseDto>builder()
                .content(courseDtos)
                .page(coursePage.getNumber())
                .size(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .last(coursePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public CourseDetailDto getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        
        if (course.isDraft()) {
            throw new EntityNotFoundException("Course not found or is not published yet");
        }
        
        return mapToCourseDetailDto(course);
    }

    @Transactional
    public CourseDto createCourse(CourseCreateDto courseCreateDto, String username) {
        User teacher = userRepository.findByUsername(username);
        if (teacher == null) {
            throw new EntityNotFoundException("Teacher not found with username: " + username);
        }
        
        Course course = Course.builder()
                .name(courseCreateDto.getName())
                .category(courseCreateDto.getCategory())
                .price(courseCreateDto.getPrice())
                .isFree(courseCreateDto.isFree())
                .isDraft(courseCreateDto.isDraft())
                .estimatedWeeks(courseCreateDto.getEstimatedWeeks())
                .summary(courseCreateDto.getSummary())
                .description(courseCreateDto.getDescription())
                .thumbnailUrl(courseCreateDto.getThumbnailUrl())
                .teacher(teacher)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        return mapToCourseDto(savedCourse);
    }

    @Transactional
    public CourseDto updateCourse(Long courseId, CourseCreateDto courseUpdateDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        
        course.setName(courseUpdateDto.getName());
        course.setCategory(courseUpdateDto.getCategory());
        course.setPrice(courseUpdateDto.getPrice());
        course.setFree(courseUpdateDto.isFree());
        course.setDraft(courseUpdateDto.isDraft());
        course.setEstimatedWeeks(courseUpdateDto.getEstimatedWeeks());
        course.setSummary(courseUpdateDto.getSummary());
        course.setDescription(courseUpdateDto.getDescription());
        course.setThumbnailUrl(courseUpdateDto.getThumbnailUrl());
        
        Course updatedCourse = courseRepository.save(course);
        return mapToCourseDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        
        courseRepository.delete(course);
    }
    
    private CourseDto mapToCourseDto(Course course) {
        int numberOfLessons = 0;
        int totalDurationMinutes = 0;
        
        if (course.getWeeks() != null) {
            for (Week week : course.getWeeks()) {
                numberOfLessons += week.getLearningItems().size();
                totalDurationMinutes += week.getLearningItems().stream()
                        .mapToInt(LearningItem::getDurationMinutes)
                        .sum();
            }
        }
        
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .category(course.getCategory())
                .price(course.getPrice())
                .isFree(course.isFree())
                .isDraft(course.isDraft())
                .numberOfLessons(numberOfLessons)
                .totalDurationMinutes(totalDurationMinutes)
                .estimatedWeeks(course.getEstimatedWeeks())
                .summary(course.getSummary())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .teacherName(course.getTeacher() != null ? course.getTeacher().getName() : null)
                .build();
    }
    
    private CourseDetailDto mapToCourseDetailDto(Course course) {
        List<Week> weeks = weekRepository.findByCourseIdOrderByWeekNumber(course.getId());
        
        List<WeekDto> weekDtos = weeks.stream().map(week -> {
            List<LearningItem> learningItems = learningItemRepository.findByWeekIdOrderByOrderIndex(week.getId());
            
            List<LearningItemDto> learningItemDtos = learningItems.stream().map(item -> 
                new LearningItemDto(
                    item.getId(),
                    item.getTitle(),
                    item.getType(),
                    item.getContent(),
                    item.getDurationMinutes(),
                    item.getOrderIndex(),
                    week.getId(),
                    week.getTitle(),
                    List.of()
                )
            ).collect(Collectors.toList());
            
            return WeekDto.builder()
                    .id(week.getId())
                    .title(week.getTitle())
                    .description(week.getDescription())
                    .weekNumber(week.getWeekNumber())
                    .learningItems(learningItemDtos)
                    .build();
        }).collect(Collectors.toList());
        
        int numberOfLessons = 0;
        int totalDurationMinutes = 0;
        
        if (weeks != null) {
            for (Week week : weeks) {
                List<LearningItem> learningItems = learningItemRepository.findByWeekIdOrderByOrderIndex(week.getId());
                numberOfLessons += learningItems.size();
                totalDurationMinutes += learningItems.stream()
                        .mapToInt(LearningItem::getDurationMinutes)
                        .sum();
            }
        }
        
        return CourseDetailDto.builder()
                .id(course.getId())
                .name(course.getName())
                .category(course.getCategory())
                .price(course.getPrice())
                .isFree(course.isFree())
                .isDraft(course.isDraft())
                .numberOfLessons(numberOfLessons)
                .totalDurationMinutes(totalDurationMinutes)
                .estimatedWeeks(course.getEstimatedWeeks())
                .summary(course.getSummary())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .teacherName(course.getTeacher() != null ? course.getTeacher().getName() : null)
                .weeks(weekDtos)
                .build();
    }
}