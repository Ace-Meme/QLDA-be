// package com.example.configuration;

// import com.example.model.*;
// import com.example.repository.QuestionRepository;
// import com.example.repository.QuizBankRepository;
// import com.example.repository.UserRepository;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// @Configuration
// @RequiredArgsConstructor
// public class QuizDataLoader {

//     private final QuizBankRepository quizBankRepository;
//     private final QuestionRepository questionRepository;
//     private final UserRepository userRepository;
//     private final ObjectMapper objectMapper;

//     @Bean
//     @Profile("!prod")
//     @Transactional
//     public CommandLineRunner initQuizData() {
//         return args -> {
//             // Get a teacher user
//             User teacher = userRepository.findByUsername("teacher");
            
//             if (teacher == null) {
//                 System.out.println("Teacher user not found, skipping quiz data creation");
//                 return;
//             }
            
//             // Create a quiz bank
//             QuizBank quizBank = QuizBank.builder()
//                     .title("Programming Fundamentals Quiz")
//                     .description("Test your knowledge of basic programming concepts")
//                     .createdBy(teacher)
//                     .creationDate(LocalDateTime.now())
//                     .lastModifiedDate(LocalDateTime.now())
//                     .active(true)
//                     .build();
            
//             quizBank = quizBankRepository.save(quizBank);
            
//             // Create 10 questions for the quiz bank
//             List<Question> questions = new ArrayList<>();
            
//             // Question 1
//             questions.add(createQuestion(
//                 quizBank,
//                 "What does HTML stand for?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "Hyper Text Markup Language",
//                     "High Tech Machine Learning",
//                     "Home Tool Management Language",
//                     "Hyperlink Text Management Layer"
//                 ),
//                 "Hyper Text Markup Language"
//             ));
            
//             // Question 2
//             questions.add(createQuestion(
//                 quizBank,
//                 "Which of the following is NOT a programming language?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "Java",
//                     "Python",
//                     "HTML",
//                     "C++"
//                 ),
//                 "HTML"
//             ));
            
//             // Question 3
//             questions.add(createQuestion(
//                 quizBank,
//                 "What does CSS stand for?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "Cascading Style Sheets",
//                     "Computer Style Syntax",
//                     "Creative Style System",
//                     "Colorful Style Sheets"
//                 ),
//                 "Cascading Style Sheets"
//             ));
            
//             // Question 4
//             questions.add(createQuestion(
//                 quizBank,
//                 "Which symbol is used for single-line comments in JavaScript?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "//",
//                     "/*",
//                     "#",
//                     "--"
//                 ),
//                 "//"
//             ));
            
//             // Question 5
//             questions.add(createQuestion(
//                 quizBank,
//                 "What is the correct syntax for referring to an external script called 'script.js'?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "<script src=\"script.js\">",
//                     "<script href=\"script.js\">",
//                     "<script name=\"script.js\">",
//                     "<script file=\"script.js\">"
//                 ),
//                 "<script src=\"script.js\">"
//             ));
            
//             // Question 6
//             questions.add(createQuestion(
//                 quizBank,
//                 "Which method can be used to find the length of a string in JavaScript?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "length()",
//                     "size()",
//                     "count()",
//                     "getSize()"
//                 ),
//                 "length()"
//             ));
            
//             // Question 7
//             questions.add(createQuestion(
//                 quizBank,
//                 "In Java, which keyword is used to inherit a class?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "extends",
//                     "implements",
//                     "inherits",
//                     "using"
//                 ),
//                 "extends"
//             ));
            
//             // Question 8
//             questions.add(createQuestion(
//                 quizBank,
//                 "Which of the following is used to declare a constant in JavaScript?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "const",
//                     "let",
//                     "var",
//                     "constant"
//                 ),
//                 "const"
//             ));
            
//             // Question 9
//             questions.add(createQuestion(
//                 quizBank,
//                 "What is the correct way to create a function in JavaScript?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "function myFunction() {}",
//                     "create myFunction() {}",
//                     "function:myFunction() {}",
//                     "def myFunction() {}"
//                 ),
//                 "function myFunction() {}"
//             ));
            
//             // Question 10
//             questions.add(createQuestion(
//                 quizBank,
//                 "Which of the following methods is used to add an element at the end of an array in JavaScript?",
//                 QuestionType.MULTIPLE_CHOICE,
//                 List.of(
//                     "push()",
//                     "append()",
//                     "addLast()",
//                     "insertEnd()"
//                 ),
//                 "push()"
//             ));
            
//             questionRepository.saveAll(questions);
            
//             System.out.println("Mock quiz data loaded successfully: 1 quiz bank with 10 questions");
//         };
//     }
    
//     private Question createQuestion(QuizBank quizBank, String questionText, QuestionType questionType, 
//                                    List<String> options, String correctAnswer) {
//         try {
//             String optionsJson = objectMapper.writeValueAsString(options);
            
//             return Question.builder()
//                     .quizBank(quizBank)
//                     .questionText(questionText)
//                     .questionType(questionType)
//                     .options(optionsJson)
//                     .correctAnswer(correctAnswer)
//                     .build();
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException("Error creating question: " + e.getMessage(), e);
//         }
//     }
// } 