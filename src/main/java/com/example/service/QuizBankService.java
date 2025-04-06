package com.example.service;

import com.example.dto.QuizBankDTO;
import com.example.dto.QuizBankCreateDTO;
import com.example.dto.QuizBankUpdateDTO;
import com.example.model.QuizBank;
import com.example.model.User;
import com.example.repository.QuizBankRepository;
import com.example.repository.QuestionRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizBankService {

    @Autowired
    private QuizBankRepository quizBankRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public QuizBankDTO createQuizBank(QuizBankCreateDTO createDTO) {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userRepository.findByUsername(username);
        
        if (creator == null) {
            throw new IllegalArgumentException("Authenticated user not found");
        }
        
        QuizBank quizBank = QuizBank.builder()
                .title(createDTO.title())
                .description(createDTO.description())
                .createdBy(creator)
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .active(true)
                .build();
        
        QuizBank savedQuizBank = quizBankRepository.save(quizBank);
        
        return mapToDTO(savedQuizBank, 0L);
    }

    public QuizBankDTO getQuizBankById(Long id) {
        QuizBank quizBank = quizBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz bank not found"));
        
        Long questionCount = questionRepository.countByQuizBankId(id);
        
        return mapToDTO(quizBank, questionCount);
    }

    public List<QuizBankDTO> getAllQuizBanks() {
        List<QuizBank> quizBanks = quizBankRepository.findAllActive();
        
        return quizBanks.stream()
                .map(quizBank -> {
                    Long questionCount = questionRepository.countByQuizBankId(quizBank.getId());
                    return mapToDTO(quizBank, questionCount);
                })
                .collect(Collectors.toList());
    }

    public List<QuizBankDTO> getQuizBanksByTeacherId(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        
        List<QuizBank> quizBanks = quizBankRepository.findByCreatedByAndActiveIsTrue(teacher);
        
        return quizBanks.stream()
                .map(quizBank -> {
                    Long questionCount = questionRepository.countByQuizBankId(quizBank.getId());
                    return mapToDTO(quizBank, questionCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizBankDTO updateQuizBank(Long id, QuizBankUpdateDTO updateDTO) {
        QuizBank quizBank = quizBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz bank not found"));
        
        quizBank.setTitle(updateDTO.title());
        quizBank.setDescription(updateDTO.description());
        quizBank.setLastModifiedDate(LocalDateTime.now());
        quizBank.setActive(updateDTO.active());
        
        QuizBank updatedQuizBank = quizBankRepository.save(quizBank);
        
        Long questionCount = questionRepository.countByQuizBankId(id);
        
        return mapToDTO(updatedQuizBank, questionCount);
    }

    @Transactional
    public void deleteQuizBank(Long id) {
        QuizBank quizBank = quizBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz bank not found"));
        
        quizBank.setActive(false);
        quizBankRepository.save(quizBank);
    }
    
    private QuizBankDTO mapToDTO(QuizBank quizBank, Long questionCount) {
        return new QuizBankDTO(
                quizBank.getId(),
                quizBank.getTitle(),
                quizBank.getDescription(),
                quizBank.getCreatedBy().getId(),
                quizBank.getCreatedBy().getName(),
                quizBank.getCreationDate(),
                quizBank.getLastModifiedDate(),
                quizBank.isActive(),
                questionCount
        );
    }
} 