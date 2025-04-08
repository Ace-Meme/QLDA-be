package com.example.service;

import com.example.dto.CreateQuestionRequest;
import com.example.dto.QuestionResponse;
import com.example.model.Question;
import com.example.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponse createQuestion(String lecturerUsername, CreateQuestionRequest request) {
        Question question = Question.builder()
                .content(request.getContent())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer())
                .categoryType(request.getCategoryType())
                .showAnswer(false)
                .createdByLecturerUsername(lecturerUsername)
                .build();

        question.setBankId(null);

        Question savedQuestion = questionRepository.save(question);

        savedQuestion.setBankId(savedQuestion.getId());
        questionRepository.save(savedQuestion);

        return new QuestionResponse(savedQuestion.getId(), "Question created successfully");
    }
}
