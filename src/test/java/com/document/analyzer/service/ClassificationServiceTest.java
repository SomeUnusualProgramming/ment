package com.document.analyzer.service;

import com.document.analyzer.entity.Classification;
import com.document.analyzer.entity.Document;
import com.document.analyzer.entity.User;
import com.document.analyzer.repository.ClassificationRepository;
import com.document.analyzer.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassificationServiceTest {

    @Mock
    private ClassificationRepository classificationRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private ClassificationService classificationService;

    private Document testDocument;
    private Classification testClassification;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.UserRole.ANALYST)
                .active(true)
                .build();

        testDocument = Document.builder()
                .id(1L)
                .fileName("contract.pdf")
                .filePath("/uploads/contract.pdf")
                .fileSize(1024L)
                .documentType(Document.DocumentType.PDF)
                .uploadedBy(testUser)
                .processingStatus(Document.ProcessingStatus.COMPLETED)
                .build();

        testClassification = Classification.builder()
                .id(1L)
                .document(testDocument)
                .category(Classification.DocumentCategory.CONTRACT)
                .confidence(0.95f)
                .classificationReason("Identified as contract based on content analysis")
                .version(1)
                .build();
    }

    @Test
    void testClassifyDocument() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(classificationRepository.save(any(Classification.class))).thenReturn(testClassification);

        Classification classification = classificationService.classifyDocument(1L);

        assertNotNull(classification);
        assertEquals(Classification.DocumentCategory.CONTRACT, classification.getCategory());
        verify(documentRepository, times(1)).findById(1L);
        verify(classificationRepository, times(1)).save(any(Classification.class));
    }

    @Test
    void testGetLatestClassification() {
        when(classificationRepository.findByDocumentId(1L)).thenReturn(Optional.of(testClassification));

        Optional<Classification> classification = classificationService.getLatestClassification(1L);

        assertTrue(classification.isPresent());
        assertEquals(Classification.DocumentCategory.CONTRACT, classification.get().getCategory());
    }

    @Test
    void testIsClassificationAccurate() {
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(testClassification));

        boolean isAccurate = classificationService.isClassificationAccurate(1L);

        assertTrue(isAccurate);
    }

    @Test
    void testIsClassificationNotAccurate() {
        testClassification.setConfidence(0.70f);
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(testClassification));

        boolean isAccurate = classificationService.isClassificationAccurate(1L);

        assertFalse(isAccurate);
    }

    @Test
    void testUpdateClassification() {
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(testClassification));
        when(classificationRepository.save(any(Classification.class))).thenReturn(testClassification);

        Classification updated = classificationService.updateClassification(1L, Classification.DocumentCategory.INVOICE);

        assertNotNull(updated);
        verify(classificationRepository, times(1)).save(any(Classification.class));
    }

    @Test
    void testDeleteClassification() {
        classificationService.deleteClassification(1L);

        verify(classificationRepository, times(1)).deleteById(1L);
    }
}
