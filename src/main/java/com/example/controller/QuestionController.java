package com.example.controller;

import com.example.model.Question;
import com.example.repository.QuestionRepository;
import com.example.security.dto.AuthenticatedUserDto;
import com.example.service.QuestionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.dto.CreateQuestionRequest;
import com.example.dto.QuestionResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
@Tag(name = "Question Management", description = "APIs for managing questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/create")
    public QuestionResponse createQuestion(
            @AuthenticationPrincipal AuthenticatedUserDto authenticatedUser,
            @RequestBody CreateQuestionRequest request
    ) {
        return questionService.createQuestion(authenticatedUser.getUsername(), request);
    }
}
