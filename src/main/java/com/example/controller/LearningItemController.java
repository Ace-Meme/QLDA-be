package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.LearningItemCreateDto;
import com.example.dto.LearningItemDto;
import com.example.dto.LearningItemUpdateDto;
import com.example.model.LearningItemType;
import com.example.service.LearningItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning-items")
@Tag(name = "Learning Item Management", description = "APIs for managing learning resources such as lectures, readings and assignments")
public class LearningItemController {

    @Autowired
    private LearningItemService learningItemService;

    @Operation(
        summary = "Create learning item",
        description = "Create a new learning item (lecture, reading, assignment, etc.) within a course week"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<LearningItemDto>> createLearningItem(@RequestBody @Valid LearningItemCreateDto createDto) {
        try {
            LearningItemDto learningItemDto = learningItemService.createLearningItem(createDto);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning item created successfully", learningItemDto), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to create learning item: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get learning item by ID",
        description = "Retrieve a learning item by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningItemDto>> getLearningItemById(
            @Parameter(description = "ID of the learning item") @PathVariable Long id) {
        try {
            LearningItemDto learningItemDto = learningItemService.getLearningItemById(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning item retrieved successfully", learningItemDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get learning items by week",
        description = "Retrieve all learning items for a specific course week"
    )
    @GetMapping("/week/{weekId}")
    public ResponseEntity<ApiResponse<List<LearningItemDto>>> getLearningItemsByWeek(
            @Parameter(description = "ID of the course week") @PathVariable Long weekId) {
        try {
            List<LearningItemDto> learningItems = learningItemService.getLearningItemsByWeek(weekId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning items retrieved successfully", learningItems), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Get learning items by week and type",
        description = "Retrieve learning items of a specific type (LECTURE, READING, ASSIGNMENT, etc.) for a course week"
    )
    @GetMapping("/week/{weekId}/type/{type}")
    public ResponseEntity<ApiResponse<List<LearningItemDto>>> getLearningItemsByWeekAndType(
            @Parameter(description = "ID of the course week") @PathVariable Long weekId, 
            @Parameter(description = "Type of learning item (LECTURE, READING, ASSIGNMENT, etc.)") @PathVariable LearningItemType type) {
        try {
            List<LearningItemDto> learningItems = learningItemService.getLearningItemsByWeekAndType(weekId, type);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning items of type " + type + " retrieved successfully", learningItems), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Update learning item",
        description = "Update an existing learning item's title, content, type, or other properties"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningItemDto>> updateLearningItem(
            @Parameter(description = "ID of the learning item to update") @PathVariable Long id, 
            @RequestBody @Valid LearningItemUpdateDto updateDto) {
        try {
            LearningItemDto updatedItem = learningItemService.updateLearningItem(id, updateDto);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning item updated successfully", updatedItem), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Reorder learning items",
        description = "Change the order of learning items within a course week"
    )
    @PostMapping("/week/{weekId}/reorder")
    public ResponseEntity<ApiResponse<List<LearningItemDto>>> reorderLearningItems(
            @Parameter(description = "ID of the course week") @PathVariable Long weekId, 
            @Parameter(description = "Ordered list of learning item IDs") @RequestBody List<Long> itemIds) {
        try {
            List<LearningItemDto> reorderedItems = learningItemService.reorderLearningItems(weekId, itemIds);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning items reordered successfully", reorderedItems), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Delete learning item",
        description = "Delete a learning item by its ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLearningItem(
            @Parameter(description = "ID of the learning item to delete") @PathVariable Long id) {
        try {
            learningItemService.deleteLearningItem(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Learning item deleted successfully", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 