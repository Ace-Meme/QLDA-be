package com.example.service;

import com.example.dto.QuestionCreateDTO;
import com.example.dto.QuestionDTO;
import com.example.dto.QuestionUpdateDTO;
import com.example.model.Question;
import com.example.model.QuizBank;
import com.example.repository.QuestionRepository;
import com.example.repository.QuizBankRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizBankRepository quizBankRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public QuestionDTO createQuestion(QuestionCreateDTO questionCreateDTO) {
        QuizBank quizBank = quizBankRepository.findById(questionCreateDTO.quizBankId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz bank not found"));
        
        String optionsJson;
        try {
            optionsJson = objectMapper.writeValueAsString(questionCreateDTO.options());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing options: " + e.getMessage());
        }
        
        Question question = Question.builder()
                .quizBank(quizBank)
                .questionText(questionCreateDTO.questionText())
                .questionType(questionCreateDTO.questionType())
                .options(optionsJson)
                .correctAnswer(questionCreateDTO.correctAnswer())
                .build();
        
        Question savedQuestion = questionRepository.save(question);
        
        return mapToDTO(savedQuestion);
    }

    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        
        return mapToDTO(question);
    }

    public List<QuestionDTO> getQuestionsByQuizBankId(Long quizBankId) {
        List<Question> questions = questionRepository.findByQuizBankId(quizBankId);
        
        return questions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getRandomQuestionsByQuizBankId(Long quizBankId, Integer count) {
        List<Question> allQuestions = questionRepository.findRandomQuestionsByQuizBankId(quizBankId);
        
        int actualCount = Math.min(count, allQuestions.size());
        List<Question> randomQuestions = allQuestions.subList(0, actualCount);
        
        return randomQuestions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionUpdateDTO questionUpdateDTO) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        
        String optionsJson;
        try {
            optionsJson = objectMapper.writeValueAsString(questionUpdateDTO.options());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing options: " + e.getMessage());
        }
        
        question.setQuestionText(questionUpdateDTO.questionText());
        question.setQuestionType(questionUpdateDTO.questionType());
        question.setOptions(optionsJson);
        question.setCorrectAnswer(questionUpdateDTO.correctAnswer());
        
        Question updatedQuestion = questionRepository.save(question);
        
        return mapToDTO(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
    
    private QuestionDTO mapToDTO(Question question) {
        List<String> options;
        try {
            options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            options = new ArrayList<>();
        }
        
        return new QuestionDTO(
                question.getId(),
                question.getQuizBank().getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                options,
                question.getCorrectAnswer()
        );
    }
} 