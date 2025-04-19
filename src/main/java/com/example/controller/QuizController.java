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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Tag(name = "Quiz Taking", description = "API endpoints for taking quizzes, submitting answers, and retrieving quiz results")
public class QuizController {

    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Autowired
    private UserRepository userRepository;

    @Operation(
        summary = "Start a quiz attempt", 
        description = "Initiate a new quiz attempt for a specific learning item. The student ID is automatically retrieved from the authenticated user. Returns the created quiz attempt with its unique ID that should be used in subsequent API calls."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Quiz attempt created successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request, missing learning item ID, learning item is not a quiz, or quiz already in progress"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - user not authenticated"
        )
    })
    @PostMapping("/attempt")
    public ResponseEntity<ApiResponse<QuizAttemptDTO>> startQuizAttempt(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Request body containing the ID of the learning item (quiz) to attempt",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        requiredProperties = {"learningItemId"},
                        example = "{\"learningItemId\": 123}"
                    )
                )
            )
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

    @Operation(
        summary = "Submit all answers", 
        description = "Submit multiple answers for questions in an active quiz attempt at once. Each answer should include the questionId and the selectedAnswer. " +
                      "The answers are automatically graded, and points are awarded based on correctness. " +
                      "Returns the list of processed student responses with correctness status."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "List of question IDs and selected answers. Each answer should include questionId (Long) and selectedAnswer (String).",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(requiredProperties = {"questionId", "selectedAnswer"})),
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Answers submitted and graded successfully. Returns list of graded student responses.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request, empty answers, questions already answered, or quiz attempt is completed"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Quiz attempt not found or questions not found"
        )
    })
    @PutMapping("/attempt/{quizAttemptId}/answers")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> submitAllAnswers(
            @Parameter(
                description = "ID of the quiz attempt", 
                example = "42", 
                required = true
            ) 
            @PathVariable Long quizAttemptId,
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

    @Operation(
        summary = "Complete a quiz attempt", 
        description = "Mark a quiz attempt as completed, calculate final score, and get the results. " +
                      "Once completed, no more answers can be submitted for this attempt. " +
                      "Returns detailed quiz results including score, percentage, and all responses."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Quiz completed successfully. Returns quiz results with total score and breakdown of answers.",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request or quiz attempt already completed"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Quiz attempt not found"
        )
    })
    @PutMapping("/attempt/{quizAttemptId}/complete")
    public ResponseEntity<ApiResponse<QuizResultDTO>> completeQuizAttempt(
            @Parameter(
                description = "ID of the quiz attempt to complete",
                required = true, 
                example = "42"
            ) 
            @PathVariable Long quizAttemptId) {
        try {
            QuizResultDTO result = quizAttemptService.completeQuizAttempt(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz completed successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
        summary = "Get quiz attempt details", 
        description = "Retrieve details of a specific quiz attempt including current status, start time, and other metadata. " +
                      "This endpoint can be used to get information about both in-progress and completed attempts."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Quiz attempt retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Quiz attempt not found"
        )
    })
    @GetMapping("/attempt/{quizAttemptId}")
    public ResponseEntity<ApiResponse<QuizAttemptDTO>> getQuizAttemptById(
            @Parameter(
                description = "ID of the quiz attempt to retrieve",
                required = true,
                example = "42"
            ) 
            @PathVariable Long quizAttemptId) {
        try {
            QuizAttemptDTO quizAttempt = quizAttemptService.getQuizAttemptById(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz attempt retrieved successfully", quizAttempt), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get quiz results", 
        description = "Retrieve detailed results of a completed quiz attempt including total score, percentage, " +
                      "and a breakdown of all responses with correctness indication. " +
                      "This endpoint is typically used after a quiz has been completed."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Quiz results retrieved successfully. Returns detailed score breakdown and all submitted answers.",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Quiz attempt not found"
        )
    })
    @GetMapping("/attempt/{quizAttemptId}/results")
    public ResponseEntity<ApiResponse<QuizResultDTO>> getQuizResults(
            @Parameter(
                description = "ID of the completed quiz attempt",
                required = true,
                example = "42"
            ) 
            @PathVariable Long quizAttemptId) {
        try {
            QuizResultDTO result = quizAttemptService.getQuizResults(quizAttemptId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Quiz results retrieved successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get student's quiz attempts", 
        description = "Retrieve all quiz attempts (both in-progress and completed) for a specific student. " +
                      "Results are returned in chronological order with the most recent attempts first."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Student's quiz attempts retrieved successfully. Returns list of all attempts.",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Student not found"
        )
    })
    @GetMapping("/student/{studentId}/attempts")
    public ResponseEntity<ApiResponse<List<QuizAttemptDTO>>> getQuizAttemptsByStudentId(
            @Parameter(
                description = "ID of the student",
                required = true,
                example = "101"
            ) 
            @PathVariable Long studentId) {
        try {
            List<QuizAttemptDTO> attempts = quizAttemptService.getQuizAttemptsByStudentId(studentId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Student's quiz attempts retrieved successfully", attempts), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get student's quiz history", 
        description = "Retrieve the complete quiz history (completed attempts only) for a specific student and learning item. " +
                      "This can be used to show a student's performance over time on a particular quiz. " +
                      "Results are ordered by attempt date with the most recent first."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Student's quiz history retrieved successfully. Returns list of completed quiz results.",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Student or learning item not found"
        )
    })
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<ApiResponse<List<QuizResultDTO>>> getStudentQuizHistory(
            @Parameter(
                description = "ID of the student",
                required = true,
                example = "101"
            ) 
            @PathVariable Long studentId,
            @Parameter(
                description = "ID of the learning item (quiz)",
                required = true,
                example = "123"
            ) 
            @RequestParam Long learningItemId) {
        try {
            List<QuizResultDTO> history = quizAttemptService.getStudentQuizHistory(studentId, learningItemId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "Student's quiz history retrieved successfully", history), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }
} 