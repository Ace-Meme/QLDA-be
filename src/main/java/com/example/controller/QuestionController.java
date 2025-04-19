package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.QuestionCreateDTO;
import com.example.dto.QuestionDTO;
import com.example.dto.QuestionUpdateDTO;
import com.example.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Management", description = "API endpoints for managing quiz questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Operation(summary = "Create a new question", description = "Create a new question for a quiz bank")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Question created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<QuestionDTO>> createQuestion(@RequestBody QuestionCreateDTO questionCreateDTO) {
        try {
            QuestionDTO createdQuestion = questionService.createQuestion(questionCreateDTO);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Question created successfully", createdQuestion), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get a question by ID", description = "Retrieve question details by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Question not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDTO>> getQuestionById(
            @Parameter(description = "ID of the question to retrieve") @PathVariable Long id) {
        try {
            QuestionDTO question = questionService.getQuestionById(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Question retrieved successfully", question), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get questions by quiz bank", description = "Retrieve all questions for a specific quiz bank")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz bank not found")
    })
    @GetMapping("/quiz-bank/{quizBankId}")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getQuestionsByQuizBankId(
            @Parameter(description = "ID of the quiz bank") @PathVariable Long quizBankId) {
        try {
            List<QuestionDTO> questions = questionService.getQuestionsByQuizBankId(quizBankId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Questions retrieved successfully", questions), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get random questions from a quiz bank", description = "Retrieve a specified number of random questions from a quiz bank")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Random questions retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz bank not found")
    })
    @GetMapping("/quiz-bank/{quizBankId}/random")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getRandomQuestionsByQuizBankId(
            @Parameter(description = "ID of the quiz bank") @PathVariable Long quizBankId,
            @Parameter(description = "Number of random questions to retrieve (default: 10)") @RequestParam(defaultValue = "10") Integer count) {
        try {
            List<QuestionDTO> questions = questionService.getRandomQuestionsByQuizBankId(quizBankId, count);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Random questions retrieved successfully", questions), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update a question", description = "Update an existing question with the provided information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or question not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDTO>> updateQuestion(
            @Parameter(description = "ID of the question to update") @PathVariable Long id, 
            @RequestBody QuestionUpdateDTO questionUpdateDTO) {
        try {
            QuestionDTO updatedQuestion = questionService.updateQuestion(id, questionUpdateDTO);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Question updated successfully", updatedQuestion), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete a question", description = "Delete a question from the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Question not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "ID of the question to delete") @PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Question deleted successfully", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }
} 