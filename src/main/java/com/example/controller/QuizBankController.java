package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.LearningItemDto;
import com.example.dto.QuizBankDTO;
import com.example.dto.QuizBankCreateDTO;
import com.example.dto.QuizBankUpdateDTO;
import com.example.dto.QuizBankLearningItemAssociationDto;
import com.example.service.QuizBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-banks")
@Tag(name = "Quiz Bank Management", description = "API endpoints for managing quiz banks")
public class QuizBankController {

    @Autowired
    private QuizBankService quizBankService;

    @Operation(summary = "Create a new quiz bank", description = "Create a new quiz bank with the provided information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Quiz bank created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<QuizBankDTO>> createQuizBank(@RequestBody QuizBankCreateDTO createDTO) {
        try {
            QuizBankDTO createdQuizBank = quizBankService.createQuizBank(createDTO);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz bank created successfully", createdQuizBank), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get a quiz bank by ID", description = "Retrieve quiz bank details by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz bank retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz bank not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizBankDTO>> getQuizBankById(
            @Parameter(description = "ID of the quiz bank to retrieve") @PathVariable Long id) {
        try {
            QuizBankDTO quizBank = quizBankService.getQuizBankById(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz bank retrieved successfully", quizBank), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all active quiz banks", description = "Retrieve a list of all active quiz banks")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz banks retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizBankDTO>>> getAllQuizBanks() {
        try {
            List<QuizBankDTO> quizBanks = quizBankService.getAllQuizBanks();
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz banks retrieved successfully", quizBanks), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get quiz banks by teacher", description = "Retrieve all quiz banks created by a specific teacher")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teacher's quiz banks retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<QuizBankDTO>>> getQuizBanksByTeacherId(
            @Parameter(description = "ID of the teacher") @PathVariable Long teacherId) {
        try {
            List<QuizBankDTO> quizBanks = quizBankService.getQuizBanksByTeacherId(teacherId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Teacher's quiz banks retrieved successfully", quizBanks), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update a quiz bank", description = "Update an existing quiz bank with the provided information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz bank updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or quiz bank not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizBankDTO>> updateQuizBank(
            @Parameter(description = "ID of the quiz bank to update") @PathVariable Long id, 
            @RequestBody QuizBankUpdateDTO updateDTO) {
        try {
            QuizBankDTO updatedQuizBank = quizBankService.updateQuizBank(id, updateDTO);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz bank updated successfully", updatedQuizBank), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete a quiz bank", description = "Mark a quiz bank as inactive (soft delete)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz bank deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz bank not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizBank(
            @Parameter(description = "ID of the quiz bank to delete") @PathVariable Long id) {
        try {
            quizBankService.deleteQuizBank(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz bank deleted successfully", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Associate quiz bank with learning item",
        description = "Associate a quiz bank with a learning item of type QUIZ. This is required before students can take the quiz."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Quiz bank associated successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request, learning item is not a quiz, or quiz bank is not active"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Quiz bank or learning item not found"
        )
    })
    @PutMapping("/{id}/learning-items")
    public ResponseEntity<ApiResponse<LearningItemDto>> associateLearningItem(
            @Parameter(description = "ID of the quiz bank", required = true) @PathVariable Long id,
            @Valid @RequestBody QuizBankLearningItemAssociationDto associationDto) {
        try {
            LearningItemDto updatedItem = quizBankService.associateLearningItem(id, associationDto);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz bank associated with learning item successfully", updatedItem), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "An unexpected error occurred: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 