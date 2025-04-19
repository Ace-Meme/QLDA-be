package com.example.service;

import com.example.dto.QuizBankDTO;
import com.example.dto.QuizBankCreateDTO;
import com.example.dto.QuizBankUpdateDTO;
import com.example.dto.QuizBankLearningItemAssociationDto;
import com.example.dto.LearningItemDto;
import com.example.model.QuizBank;
import com.example.model.LearningItem;
import com.example.model.LearningItemType;
import com.example.model.User;
import com.example.repository.QuizBankRepository;
import com.example.repository.QuestionRepository;
import com.example.repository.UserRepository;
import com.example.repository.LearningItemRepository;
import jakarta.persistence.EntityNotFoundException;
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
    
    @Autowired
    private LearningItemRepository learningItemRepository;
    
    @Autowired
    private LearningItemService learningItemService;

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

    @Transactional(readOnly = true)
    public QuizBankDTO getQuizBankById(Long id) {
        QuizBank quizBank = quizBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz bank not found"));
        
        Long questionCount = questionRepository.countByQuizBankId(id);
        
        return mapToDTO(quizBank, questionCount);
    }

    @Transactional(readOnly = true)
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
        
        boolean updated = false;

        if (updateDTO.title() != null && !updateDTO.title().isBlank() && !updateDTO.title().equals(quizBank.getTitle())) {
            quizBank.setTitle(updateDTO.title());
            updated = true;
        }
        
        if (updateDTO.description() != null && !updateDTO.description().equals(quizBank.getDescription())) {
            quizBank.setDescription(updateDTO.description());
            updated = true;
        }
        
        // Check if the active field is provided (not null) and different from the current value
        if (updateDTO.active() != null && updateDTO.active() != quizBank.isActive()) {
            quizBank.setActive(updateDTO.active());
            updated = true;
        }
        
        if (updated) {
            quizBank.setLastModifiedDate(LocalDateTime.now());
        }
        
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

    /**
     * Associate a quiz bank with a learning item
     * 
     * @param quizBankId ID of the quiz bank
     * @param associationDto The DTO containing learning item ID
     * @return The updated learning item DTO
     */
    @Transactional
    public LearningItemDto associateLearningItem(Long quizBankId, QuizBankLearningItemAssociationDto associationDto) {
        // Get the quiz bank
        QuizBank quizBank = quizBankRepository.findById(quizBankId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz bank not found with id: " + quizBankId));
                
        // Verify the quiz bank is active
        if (!quizBank.isActive()) {
            throw new IllegalArgumentException("Quiz bank is not active and cannot be associated with learning items");
        }
        
        // Get the learning item
        LearningItem learningItem = learningItemRepository.findById(associationDto.learningItemId())
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + associationDto.learningItemId()));
                
        // Check if the learning item is of type QUIZ
        if (learningItem.getType() != LearningItemType.QUIZ) {
            throw new IllegalArgumentException("Learning item is not a quiz. Only learning items of type QUIZ can be associated with quiz banks.");
        }
        
        // Set the quiz bank
        learningItem.setQuizBank(quizBank);
        
        // Save the learning item
        LearningItem updatedItem = learningItemRepository.save(learningItem);
        
        // Return the updated learning item DTO using the existing service method
        return learningItemService.getLearningItemById(updatedItem.getId());
    }
} 