package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.QuizAttemptDTO;
import com.example.dto.StudentResponseDTO;
import com.example.dto.QuizResultDTO;
import com.example.service.QuizAttemptService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Taking", description = "API endpoints for taking quizzes and managing quiz attempts")
public class QuizController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Operation(summary = "Start a quiz attempt", description = "Start a new quiz attempt for a specific learning item")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Quiz attempt started successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or quiz already in progress")
    })
    @PostMapping("/attempt")
    public ResponseEntity<ApiResponse<QuizAttemptDTO>> startQuizAttempt(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Student ID and learning item ID")
            @RequestBody Map<String, Long> request) {
        try {
            Long studentId = request.get("studentId");
            Long learningItemId = request.get("learningItemId");
            
            if (studentId == null || learningItemId == null) {
                throw new IllegalArgumentException("Student ID and learning item ID must be provided");
            }
            
            QuizAttemptDTO quizAttempt = quizAttemptService.startQuizAttempt(studentId, learningItemId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz attempt started successfully", quizAttempt), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Submit an answer", description = "Submit an answer for a question in an active quiz attempt")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Answer submitted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or answer")
    })
    @PutMapping("/attempt/{quizAttemptId}/answer")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> submitAnswer(
            @Parameter(description = "ID of the quiz attempt") @PathVariable Long quizAttemptId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Question ID and selected answer")
            @RequestBody Map<String, Object> request) {
        try {
            Long questionId = ((Number) request.get("questionId")).longValue();
            String selectedAnswer = (String) request.get("selectedAnswer");
            
            if (questionId == null || selectedAnswer == null) {
                throw new IllegalArgumentException("Question ID and selected answer must be provided");
            }
            
            StudentResponseDTO response = quizAttemptService.submitAnswer(quizAttemptId, questionId, selectedAnswer);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Answer submitted successfully", response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Complete a quiz attempt", description = "Mark a quiz attempt as completed and get the results")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz completed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or quiz already completed")
    })
    @PutMapping("/attempt/{quizAttemptId}/complete")
    public ResponseEntity<ApiResponse<QuizResultDTO>> completeQuizAttempt(
            @Parameter(description = "ID of the quiz attempt to complete") @PathVariable Long quizAttemptId) {
        try {
            QuizResultDTO result = quizAttemptService.completeQuizAttempt(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz completed successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get quiz attempt details", description = "Retrieve details of a specific quiz attempt")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz attempt retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz attempt not found")
    })
    @GetMapping("/attempt/{quizAttemptId}")
    public ResponseEntity<ApiResponse<QuizAttemptDTO>> getQuizAttemptById(
            @Parameter(description = "ID of the quiz attempt") @PathVariable Long quizAttemptId) {
        try {
            QuizAttemptDTO quizAttempt = quizAttemptService.getQuizAttemptById(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz attempt retrieved successfully", quizAttempt), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get quiz results", description = "Retrieve the results of a completed quiz attempt")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quiz results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Quiz attempt not found")
    })
    @GetMapping("/attempt/{quizAttemptId}/results")
    public ResponseEntity<ApiResponse<QuizResultDTO>> getQuizResults(
            @Parameter(description = "ID of the quiz attempt") @PathVariable Long quizAttemptId) {
        try {
            QuizResultDTO result = quizAttemptService.getQuizResults(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz results retrieved successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get student's quiz attempts", description = "Retrieve all quiz attempts for a specific student")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student's quiz attempts retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}/attempts")
    public ResponseEntity<ApiResponse<List<QuizAttemptDTO>>> getQuizAttemptsByStudentId(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        try {
            List<QuizAttemptDTO> attempts = quizAttemptService.getQuizAttemptsByStudentId(studentId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Student's quiz attempts retrieved successfully", attempts), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get student's quiz history", description = "Retrieve quiz history for a specific student and learning item")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student's quiz history retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student or learning item not found")
    })
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<ApiResponse<List<QuizResultDTO>>> getStudentQuizHistory(
            @Parameter(description = "ID of the student") @PathVariable Long studentId,
            @Parameter(description = "ID of the learning item") @RequestParam Long learningItemId) {
        try {
            List<QuizResultDTO> history = quizAttemptService.getStudentQuizHistory(studentId, learningItemId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Student's quiz history retrieved successfully", history), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }
} 