package com.example.service;

import com.example.dto.QuizAttemptDTO;
import com.example.dto.StudentResponseDTO;
import com.example.dto.QuizResultDTO;
import com.example.model.*;
import com.example.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private StudentResponseRepository studentResponseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LearningItemRepository learningItemRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public QuizAttemptDTO startQuizAttempt(Long studentId, Long learningItemId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        LearningItem learningItem = learningItemRepository.findById(learningItemId)
                .orElseThrow(() -> new IllegalArgumentException("Learning item not found"));
        
        if (learningItem.getType() != LearningItemType.QUIZ) {
            throw new IllegalArgumentException("Learning item is not a quiz");
        }
        
        QuizBank quizBank = learningItem.getQuizBank();
        if (quizBank == null) {
            throw new IllegalArgumentException("No quiz bank associated with this learning item");
        }
        
        // Check if there's an in-progress attempt
        quizAttemptRepository.findByStudentAndLearningItemAndStatus(student, learningItem, QuizAttemptStatus.IN_PROGRESS)
                .ifPresent(existingAttempt -> {
                    throw new IllegalArgumentException("There is already an in-progress quiz attempt");
                });
        
        // Create new attempt
        QuizAttempt quizAttempt = QuizAttempt.builder()
                .student(student)
                .quizBank(quizBank)
                .learningItem(learningItem)
                .startTime(LocalDateTime.now())
                .status(QuizAttemptStatus.IN_PROGRESS)
                .build();
        
        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);
        
        return mapToDTO(savedAttempt);
    }

    @Transactional
    public StudentResponseDTO submitAnswer(Long quizAttemptId, Long questionId, String selectedAnswer) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz attempt not found"));
        
        if (quizAttempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("This quiz attempt is already completed");
        }
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        
        // Verify the question belongs to the quiz bank
        if (!question.getQuizBank().getId().equals(quizAttempt.getQuizBank().getId())) {
            throw new IllegalArgumentException("Question does not belong to this quiz");
        }
        
        // Check if question already answered
        List<StudentResponse> existingResponses = studentResponseRepository.findByQuizAttempt(quizAttempt);
        for (StudentResponse existingResponse : existingResponses) {
            if (existingResponse.getQuestion().getId().equals(questionId)) {
                throw new IllegalArgumentException("Question already answered");
            }
        }
        
        // Grade the answer - each correct answer is worth 1 point
        boolean isCorrect = question.getCorrectAnswer().equals(selectedAnswer);
        int pointsEarned = isCorrect ? 1 : 0;
        
        // Save response
        StudentResponse response = StudentResponse.builder()
                .quizAttempt(quizAttempt)
                .question(question)
                .selectedAnswer(selectedAnswer)
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .build();
        
        StudentResponse savedResponse = studentResponseRepository.save(response);
        
        return mapToResponseDTO(savedResponse);
    }

    @Transactional
    public List<StudentResponseDTO> submitAllAnswers(Long quizAttemptId, List<Map<String, Object>> answers) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz attempt not found"));
        
        if (quizAttempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("This quiz attempt is already completed");
        }
        
        List<StudentResponse> existingResponses = studentResponseRepository.findByQuizAttempt(quizAttempt);
        
        List<StudentResponseDTO> responseList = answers.stream()
                .map(answer -> {
                    Long questionId = ((Number) answer.get("questionId")).longValue();
                    String selectedAnswer = (String) answer.get("selectedAnswer");
                    
                    if (questionId == null || selectedAnswer == null) {
                        throw new IllegalArgumentException("Question ID and selected answer must be provided for all answers");
                    }
                    
                    // Check if question already answered
                    for (StudentResponse existingResponse : existingResponses) {
                        if (existingResponse.getQuestion().getId().equals(questionId)) {
                            throw new IllegalArgumentException("Question " + questionId + " already answered");
                        }
                    }
                    
                    Question question = questionRepository.findById(questionId)
                            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
                    
                    // Verify the question belongs to the quiz bank
                    if (!question.getQuizBank().getId().equals(quizAttempt.getQuizBank().getId())) {
                        throw new IllegalArgumentException("Question " + questionId + " does not belong to this quiz");
                    }
                    
                    // Grade the answer - each correct answer is worth 1 point
                    boolean isCorrect = question.getCorrectAnswer().equals(selectedAnswer);
                    int pointsEarned = isCorrect ? 1 : 0;
                    
                    // Save response
                    StudentResponse response = StudentResponse.builder()
                            .quizAttempt(quizAttempt)
                            .question(question)
                            .selectedAnswer(selectedAnswer)
                            .isCorrect(isCorrect)
                            .pointsEarned(pointsEarned)
                            .build();
                    
                    StudentResponse savedResponse = studentResponseRepository.save(response);
                    return mapToResponseDTO(savedResponse);
                })
                .collect(Collectors.toList());
        
        return responseList;
    }

    @Transactional
    public QuizResultDTO completeQuizAttempt(Long quizAttemptId) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz attempt not found"));
        
        if (quizAttempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("This quiz attempt is already completed");
        }
        
        // Get all responses
        List<StudentResponse> responses = studentResponseRepository.findByQuizAttempt(quizAttempt);
        
        // Calculate total score (1 point per correct answer)
        int totalScore = 0;
        for (StudentResponse response : responses) {
            totalScore += response.getPointsEarned();
        }
        
        // Max possible score is equal to the number of questions answered
        int maxPossibleScore = responses.size();
        
        // Update quiz attempt
        quizAttempt.setEndTime(LocalDateTime.now());
        quizAttempt.setTotalScore(totalScore);
        quizAttempt.setMaxPossibleScore(maxPossibleScore);
        quizAttempt.setStatus(QuizAttemptStatus.COMPLETED);
        
        QuizAttempt completedAttempt = quizAttemptRepository.save(quizAttempt);
        
        // Return quiz results
        return getQuizResults(completedAttempt.getId());
    }

    public List<QuizAttemptDTO> getQuizAttemptsByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        List<QuizAttempt> attempts = quizAttemptRepository.findByStudent(student);
        
        return attempts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public QuizAttemptDTO getQuizAttemptById(Long id) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz attempt not found"));
        
        return mapToDTO(quizAttempt);
    }

    public QuizResultDTO getQuizResults(Long quizAttemptId) {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz attempt not found"));
        
        List<StudentResponse> responses = studentResponseRepository.findByQuizAttempt(quizAttempt);
        
        List<StudentResponseDTO> responseDTOs = responses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        
        double percentageScore = 0;
        if (quizAttempt.getMaxPossibleScore() > 0) {
            percentageScore = (double) quizAttempt.getTotalScore() / quizAttempt.getMaxPossibleScore() * 100;
        }
        
        return new QuizResultDTO(
                quizAttempt.getId(),
                quizAttempt.getQuizBank().getTitle(),
                quizAttempt.getStartTime(),
                quizAttempt.getEndTime(),
                quizAttempt.getTotalScore(),
                quizAttempt.getMaxPossibleScore(),
                percentageScore,
                responseDTOs
        );
    }

    public List<QuizResultDTO> getStudentQuizHistory(Long studentId, Long learningItemId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByStudentIdAndLearningItemId(studentId, learningItemId);
        
        return attempts.stream()
                .filter(attempt -> attempt.getStatus() == QuizAttemptStatus.COMPLETED)
                .map(attempt -> getQuizResults(attempt.getId()))
                .collect(Collectors.toList());
    }
    
    private QuizAttemptDTO mapToDTO(QuizAttempt quizAttempt) {
        return new QuizAttemptDTO(
                quizAttempt.getId(),
                quizAttempt.getStudent().getId(),
                quizAttempt.getStudent().getName(),
                quizAttempt.getQuizBank().getId(),
                quizAttempt.getQuizBank().getTitle(),
                quizAttempt.getLearningItem().getId(),
                quizAttempt.getStartTime(),
                quizAttempt.getEndTime(),
                quizAttempt.getTotalScore(),
                quizAttempt.getMaxPossibleScore(),
                quizAttempt.getStatus()
        );
    }
    
    private StudentResponseDTO mapToResponseDTO(StudentResponse response) {
        return new StudentResponseDTO(
                response.getId(),
                response.getQuizAttempt().getId(),
                response.getQuestion().getId(),
                response.getQuestion().getQuestionText(),
                response.getSelectedAnswer(),
                response.getIsCorrect(),
                response.getPointsEarned()
        );
    }
} 