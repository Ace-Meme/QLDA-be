package com.example.service;

import com.example.dto.DocumentDto;
import com.example.dto.LearningItemCreateDto;
import com.example.dto.LearningItemDto;
import com.example.dto.LearningItemUpdateDto;
import com.example.model.Document;
import com.example.model.LearningItem;
import com.example.model.LearningItemType;
import com.example.model.Week;
import com.example.repository.DocumentRepository;
import com.example.repository.LearningItemRepository;
import com.example.repository.WeekRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningItemService {

    @Autowired
    private LearningItemRepository learningItemRepository;
    
    @Autowired
    private WeekRepository weekRepository;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Create a new learning item
     * 
     * @param createDto The DTO containing learning item data
     * @return The created learning item DTO
     */
    @Transactional
    public LearningItemDto createLearningItem(LearningItemCreateDto createDto) {
        Week week = weekRepository.findById(createDto.weekId())
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + createDto.weekId()));
        
        // If no order index is provided, put it at the end
        Integer orderIndex = createDto.orderIndex();
        if (orderIndex == null || orderIndex == 0) {
            long itemCount = learningItemRepository.countByWeekId(createDto.weekId());
            orderIndex = (int) itemCount;
        }
        
        LearningItem learningItem = LearningItem.builder()
                .title(createDto.title())
                .type(createDto.type())
                .content(createDto.content())
                .durationMinutes(createDto.durationMinutes())
                .orderIndex(orderIndex)
                .week(week)
                .build();
        
        LearningItem savedItem = learningItemRepository.save(learningItem);
        
        return mapToLearningItemDto(savedItem, List.of());
    }
    
    /**
     * Get a learning item by ID
     * 
     * @param id Learning item ID
     * @return The learning item DTO
     */
    public LearningItemDto getLearningItemById(Long id) {
        LearningItem learningItem = learningItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + id));
        
        List<Document> documents = documentRepository.findByLearningItem(learningItem);
        // Ensure we only use the first document (there should be only one)
        List<DocumentDto> documentDtos = documents.isEmpty() ? 
                List.of() : 
                List.of(mapToDocumentDto(documents.get(0)));
        
        return mapToLearningItemDto(learningItem, documentDtos);
    }
    
    /**
     * Get all learning items for a specific week
     * 
     * @param weekId Week ID
     * @return List of learning item DTOs
     */
    public List<LearningItemDto> getLearningItemsByWeek(Long weekId) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
                
        List<LearningItem> learningItems = learningItemRepository.findByWeekIdOrderByOrderIndex(weekId);
        
        // Get all documents for all learning items in this week
        List<Document> allDocuments = documentRepository.findByCourseId(week.getCourse().getId());
        
        // Group documents by learning item ID - but ensure only one document per learning item
        Map<Long, Document> documentByLearningItem = allDocuments.stream()
                .filter(doc -> doc.getLearningItem() != null)
                .collect(Collectors.toMap(
                    doc -> doc.getLearningItem().getId(), 
                    doc -> doc, 
                    (existing, replacement) -> existing  // Keep first one in case of duplicates
                ));
        
        // Map each learning item to DTO with its document (if any)
        return learningItems.stream()
                .map(item -> {
                    Document itemDocument = documentByLearningItem.get(item.getId());
                    List<DocumentDto> documentDtos = (itemDocument != null) ? 
                            List.of(mapToDocumentDto(itemDocument)) : 
                            List.of();
                    return mapToLearningItemDto(item, documentDtos);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get all learning items of a specific type for a week
     * 
     * @param weekId Week ID
     * @param type Learning item type
     * @return List of learning item DTOs
     */
    public List<LearningItemDto> getLearningItemsByWeekAndType(Long weekId, LearningItemType type) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
        
        List<LearningItem> learningItems = learningItemRepository.findByWeekIdAndTypeOrderByOrderIndex(weekId, type);
        
        // Get all documents for all learning items in this week
        List<Document> allDocuments = documentRepository.findByCourseId(week.getCourse().getId());
        
        // Group documents by learning item ID - but ensure only one document per learning item
        Map<Long, Document> documentByLearningItem = allDocuments.stream()
                .filter(doc -> doc.getLearningItem() != null)
                .collect(Collectors.toMap(
                    doc -> doc.getLearningItem().getId(), 
                    doc -> doc, 
                    (existing, replacement) -> existing  // Keep first one in case of duplicates
                ));
        
        // Map each learning item to DTO with its document (if any)
        return learningItems.stream()
                .map(item -> {
                    Document itemDocument = documentByLearningItem.get(item.getId());
                    List<DocumentDto> documentDtos = (itemDocument != null) ? 
                            List.of(mapToDocumentDto(itemDocument)) : 
                            List.of();
                    return mapToLearningItemDto(item, documentDtos);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Update an existing learning item
     * 
     * @param id Learning item ID
     * @param updateDto The DTO containing updated data
     * @return The updated learning item DTO
     */
    @Transactional
    public LearningItemDto updateLearningItem(Long id, LearningItemUpdateDto updateDto) {
        LearningItem learningItem = learningItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + id));
        
        learningItem.setTitle(updateDto.title());
        learningItem.setType(updateDto.type());
        learningItem.setContent(updateDto.content());
        learningItem.setDurationMinutes(updateDto.durationMinutes());
        learningItem.setOrderIndex(updateDto.orderIndex());
        
        LearningItem updatedItem = learningItemRepository.save(learningItem);
        
        List<Document> documents = documentRepository.findByLearningItem(updatedItem);
        List<DocumentDto> documentDtos = mapToDocumentDtos(documents);
        
        return mapToLearningItemDto(updatedItem, documentDtos);
    }
    
    /**
     * Reorder learning items within a week
     * 
     * @param weekId Week ID
     * @param itemIds Ordered list of learning item IDs
     * @return List of updated learning item DTOs
     */
    @Transactional
    public List<LearningItemDto> reorderLearningItems(Long weekId, List<Long> itemIds) {
        // Check if week exists
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));
        
        // Get all learning items for the week
        List<LearningItem> learningItems = learningItemRepository.findByWeekIdOrderByOrderIndex(weekId);
        
        // Create a map of id to learning item
        Map<Long, LearningItem> itemMap = learningItems.stream()
                .collect(Collectors.toMap(LearningItem::getId, item -> item));
        
        // Check if all provided ids exist in the week
        for (Long itemId : itemIds) {
            if (!itemMap.containsKey(itemId)) {
                throw new IllegalArgumentException("Learning item with id " + itemId + 
                        " does not exist in week " + weekId);
            }
        }
        
        // Update the order index based on the provided order
        List<LearningItem> updatedItems = new ArrayList<>();
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            LearningItem item = itemMap.get(itemId);
            item.setOrderIndex(i);
            updatedItems.add(item);
        }
        
        // Save all updated items
        learningItemRepository.saveAll(updatedItems);
        
        // Convert to DTOs and return
        return getLearningItemsByWeek(weekId);
    }
    
    /**
     * Delete a learning item
     * 
     * @param id Learning item ID
     */
    @Transactional
    public void deleteLearningItem(Long id) {
        LearningItem learningItem = learningItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning item not found with id: " + id));
        
        // Get all documents associated with this learning item
        List<Document> documents = documentRepository.findByLearningItem(learningItem);
        
        // Delete all associated documents
        for (Document document : documents) {
            // Delete the file from storage
            fileStorageService.deleteFile(document.getFileUrl());
            
            // Delete the document record
            documentRepository.delete(document);
        }
        
        // Delete the learning item
        learningItemRepository.delete(learningItem);
    }
    
    private LearningItemDto mapToLearningItemDto(LearningItem learningItem, List<DocumentDto> documents) {
        return new LearningItemDto(
                learningItem.getId(),
                learningItem.getTitle(),
                learningItem.getType(),
                learningItem.getContent(),
                learningItem.getDurationMinutes(),
                learningItem.getOrderIndex(),
                learningItem.getWeek().getId(),
                learningItem.getWeek().getTitle(),
                documents
        );
    }
    
    private List<DocumentDto> mapToDocumentDtos(List<Document> documents) {
        return documents.stream()
                .map(document -> new DocumentDto(
                        document.getId(),
                        document.getTitle(),
                        document.getFileName(),
                        document.getContentType(),
                        document.getFileSize(),
                        document.getFileUrl(),
                        document.getDescription(),
                        document.getUploadedAt(),
                        document.getUploadedBy().getId(),
                        document.getUploadedBy().getName(),
                        document.getLearningItem() != null ? document.getLearningItem().getId() : null,
                        document.getIsVideo()
                ))
                .collect(Collectors.toList());
    }

    private DocumentDto mapToDocumentDto(Document document) {
        return new DocumentDto(
                document.getId(),
                document.getTitle(),
                document.getFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getFileUrl(),
                document.getDescription(),
                document.getUploadedAt(),
                document.getUploadedBy().getId(),
                document.getUploadedBy().getName(),
                document.getLearningItem() != null ? document.getLearningItem().getId() : null,
                document.getIsVideo()
        );
    }
} 