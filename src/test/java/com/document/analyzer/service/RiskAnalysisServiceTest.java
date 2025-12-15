package com.document.analyzer.service;

import com.document.analyzer.entity.Document;
import com.document.analyzer.entity.RiskAnalysis;
import com.document.analyzer.entity.User;
import com.document.analyzer.repository.DocumentRepository;
import com.document.analyzer.repository.RiskAnalysisRepository;
import com.document.analyzer.repository.UserRepository;
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
public class RiskAnalysisServiceTest {

    @Mock
    private RiskAnalysisRepository riskAnalysisRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RiskAnalysisService riskAnalysisService;

    private Document testDocument;
    private RiskAnalysis testRiskAnalysis;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("analyst@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role(User.UserRole.ANALYST)
                .active(true)
                .build();

        testDocument = Document.builder()
                .id(1L)
                .fileName("policy.pdf")
                .filePath("/uploads/policy.pdf")
                .fileSize(2048L)
                .documentType(Document.DocumentType.PDF)
                .uploadedBy(testUser)
                .processingStatus(Document.ProcessingStatus.COMPLETED)
                .build();

        testRiskAnalysis = RiskAnalysis.builder()
                .id(1L)
                .document(testDocument)
                .overallRiskLevel(RiskAnalysis.RiskLevel.HIGH)
                .riskScore(0.75f)
                .identifiedRisks("Data exposure risk, compliance gaps")
                .mitigationRecommendations("Implement data encryption, update policies")
                .framework(RiskAnalysis.AnalysisFramework.OWASP)
                .reviewed(false)
                .build();
    }

    @Test
    void testAnalyzeDocumentRisk() {
        RiskAnalysis expectedAnalysis = RiskAnalysis.builder()
                .id(1L)
                .document(testDocument)
                .overallRiskLevel(RiskAnalysis.RiskLevel.MEDIUM)
                .riskScore(0.5f)
                .identifiedRisks("Moderate security concerns")
                .mitigationRecommendations("Recommended actions")
                .framework(RiskAnalysis.AnalysisFramework.OWASP)
                .reviewed(false)
                .build();
        
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(riskAnalysisRepository.save(any(RiskAnalysis.class))).thenReturn(expectedAnalysis);

        RiskAnalysis analysis = riskAnalysisService.analyzeDocumentRisk(1L, RiskAnalysis.AnalysisFramework.OWASP);

        assertNotNull(analysis);
        assertEquals(RiskAnalysis.RiskLevel.MEDIUM, analysis.getOverallRiskLevel());
        verify(documentRepository, times(1)).findById(1L);
        verify(riskAnalysisRepository, times(1)).save(any(RiskAnalysis.class));
    }

    @Test
    void testGetAnalysisForDocument() {
        when(riskAnalysisRepository.findByDocumentId(1L)).thenReturn(Optional.of(testRiskAnalysis));

        Optional<RiskAnalysis> analysis = riskAnalysisService.getAnalysisForDocument(1L);

        assertTrue(analysis.isPresent());
        assertEquals(RiskAnalysis.RiskLevel.HIGH, analysis.get().getOverallRiskLevel());
    }

    @Test
    void testGetUnreviewedAnalyses() {
        riskAnalysisService.getUnreviewedAnalyses();

        verify(riskAnalysisRepository, times(1)).findByReviewed(false);
    }

    @Test
    void testReviewAnalysis() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(riskAnalysisRepository.findById(1L)).thenReturn(Optional.of(testRiskAnalysis));
        when(riskAnalysisRepository.save(any(RiskAnalysis.class))).thenReturn(testRiskAnalysis);

        RiskAnalysis reviewed = riskAnalysisService.reviewAnalysis(1L, 1L, "Risks are acceptable with mitigations");

        assertNotNull(reviewed);
        assertTrue(reviewed.getReviewed());
        verify(riskAnalysisRepository, times(1)).save(any(RiskAnalysis.class));
    }

    @Test
    void testUpdateRiskLevel() {
        when(riskAnalysisRepository.findById(1L)).thenReturn(Optional.of(testRiskAnalysis));
        when(riskAnalysisRepository.save(any(RiskAnalysis.class))).thenReturn(testRiskAnalysis);

        RiskAnalysis updated = riskAnalysisService.updateRiskLevel(1L, RiskAnalysis.RiskLevel.CRITICAL);

        assertNotNull(updated);
        verify(riskAnalysisRepository, times(1)).save(any(RiskAnalysis.class));
    }

    @Test
    void testGetAnalysesByFramework() {
        riskAnalysisService.getAnalysesByFramework(RiskAnalysis.AnalysisFramework.OWASP);

        verify(riskAnalysisRepository, times(1)).findByFramework(RiskAnalysis.AnalysisFramework.OWASP);
    }
}
