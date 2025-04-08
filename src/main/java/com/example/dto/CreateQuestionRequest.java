package com.example.dto;

import com.example.model.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionRequest {

    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private CategoryType categoryType;
}
