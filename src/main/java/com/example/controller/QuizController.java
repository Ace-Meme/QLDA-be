package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.QuizAttemptDTO;
import com.example.dto.StudentResponseDTO;
import com.example.dto.QuizResultDTO;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.QuizAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Taking", description = "API endpoints for taking quizzes and managing quiz attempts")
public class QuizController {

    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Start a quiz attempt", description = "Start a new quiz attempt for a specific learning item")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Quiz attempt started successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or quiz already in progress")
    })
    @PostMapping("/attempt")
    public ResponseEntity<ApiResponse<QuizAttemptDTO>> startQuizAttempt(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Learning item ID for the quiz")
            @RequestBody Map<String, Long> request) {
        try {
            // Get student ID from authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User student = userRepository.findByUsername(username);
            
            if (student == null) {
                throw new IllegalArgumentException("Authenticated user not found");
            }
            
            Long studentId = student.getId();
            Long learningItemId = request.get("learningItemId");
            
            if (learningItemId == null) {
                throw new IllegalArgumentException("Learning item ID must be provided");
            }
            
            QuizAttemptDTO quizAttempt = quizAttemptService.startQuizAttempt(studentId, learningItemId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz attempt started successfully", quizAttempt), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Submit all answers", 
               description = "Submit multiple answers for questions in an active quiz attempt at once. This is more efficient than submitting answers one by one.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "List of question IDs and selected answers",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = {
                @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Multiple Quiz Answers",
                    summary = "Submitting answers for multiple questions",
                    value = """
                    [
                      {
                        "questionId": 1,
                        "selectedAnswer": "B"
                      },
                      {
                        "questionId": 2,
                        "selectedAnswer": "A"
                      },
                      {
                        "questionId": 3,
                        "selectedAnswer": "C"
                      }
                    ]
                    """
                )
            }
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Answers submitted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or answers")
    })
    @PutMapping("/attempt/{quizAttemptId}/answers")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> submitAllAnswers(
            @Parameter(description = "ID of the quiz attempt", example = "42") @PathVariable Long quizAttemptId,
            @RequestBody List<Map<String, Object>> answers) {
        try {
            if (answers == null || answers.isEmpty()) {
                throw new IllegalArgumentException("At least one answer must be provided");
            }
            
            List<StudentResponseDTO> responses = quizAttemptService.submitAllAnswers(quizAttemptId, answers);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "All answers submitted successfully", responses), HttpStatus.OK);
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