package com.example.service;

import com.example.dto.CreateQuestionRequest;
import com.example.dto.QuestionResponse;

public interface QuestionService {

    QuestionResponse createQuestion(String lecturerUsername, CreateQuestionRequest request);
}
