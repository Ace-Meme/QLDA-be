package com.example.service;

import com.example.dto.LearningItemCreateDto;
import com.example.dto.LearningItemDto;
import com.example.dto.LearningItemUpdateDto;
import com.example.model.LearningItemType;

import java.util.List;

public interface LearningItemService {
    
    /**
     * Create a new learning item
     * 
     * @param createDto The DTO containing learning item data
     * @return The created learning item DTO
     */
    LearningItemDto createLearningItem(LearningItemCreateDto createDto);
    
    /**
     * Get a learning item by ID
     * 
     * @param id Learning item ID
     * @return The learning item DTO
     */
    LearningItemDto getLearningItemById(Long id);
    
    /**
     * Get all learning items for a specific week
     * 
     * @param weekId Week ID
     * @return List of learning item DTOs
     */
    List<LearningItemDto> getLearningItemsByWeek(Long weekId);
    
    /**
     * Get all learning items of a specific type for a week
     * 
     * @param weekId Week ID
     * @param type Learning item type
     * @return List of learning item DTOs
     */
    List<LearningItemDto> getLearningItemsByWeekAndType(Long weekId, LearningItemType type);
    
    /**
     * Update an existing learning item
     * 
     * @param id Learning item ID
     * @param updateDto The DTO containing updated data
     * @return The updated learning item DTO
     */
    LearningItemDto updateLearningItem(Long id, LearningItemUpdateDto updateDto);
    
    /**
     * Reorder learning items within a week
     * 
     * @param weekId Week ID
     * @param itemIds Ordered list of learning item IDs
     * @return List of updated learning item DTOs
     */
    List<LearningItemDto> reorderLearningItems(Long weekId, List<Long> itemIds);
    
    /**
     * Delete a learning item
     * 
     * @param id Learning item ID
     */
    void deleteLearningItem(Long id);
} 