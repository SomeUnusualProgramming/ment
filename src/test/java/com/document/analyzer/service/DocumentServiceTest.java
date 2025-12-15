package com.document.analyzer.service;

import com.document.analyzer.domain.AnalysisRequest;
import com.document.analyzer.domain.AnalysisResponse;
import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import com.document.analyzer.util.DocumentProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    private DocumentService documentService;

    @Mock
    private DocumentClassifier documentClassifier;

    @Mock
    private RiskFrameworkSelector riskFrameworkSelector;

    @Mock
    private IntentAnalyzer intentAnalyzer;

    @Mock
    private SanityChecker sanityChecker;

    @Mock
    private JsonFormatter jsonFormatter;

    @Mock
    private DocumentProcessor documentProcessor;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentClassifier,
                riskFrameworkSelector,
                intentAnalyzer,
                sanityChecker,
                jsonFormatter,
                documentProcessor
        );
    }

    @Test
    void testAnalyzeWithValidRequest() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("Terms of service document")
                .fileName("tos.txt")
                .build();

        when(documentProcessor.processDocument(any(), any())).thenReturn("Terms of service document");
        when(documentClassifier.classify(any())).thenReturn(DocumentType.PLATFORM_TERMS);
        when(riskFrameworkSelector.selectRiskCategory(any())).thenReturn(RiskCategory.LEGAL_COMPLIANCE);

        LLMAnalysisResult mockResult = LLMAnalysisResult.builder()
                .summary("Analysis summary")
                .keyPoints(List.of("Point 1"))
                .risks(List.of(Map.of("level", "HIGH", "description", "Risk", "impact", "Impact")))
                .recommendations(List.of("Rec 1"))
                .confidence(0.85)
                .build();

        when(intentAnalyzer.analyze(any(), any(), any())).thenReturn(mockResult);

        SanityChecker.SanityCheckResult sanityResult = new SanityChecker.SanityCheckResult(true, List.of());
        when(sanityChecker.check(any())).thenReturn(sanityResult);

        AnalysisResponse mockResponse = AnalysisResponse.builder()
                .documentType("Platform Terms")
                .riskCategory("Legal & Compliance")
                .summary("Analysis summary")
                .keyPoints(List.of("Point 1"))
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .build();

        when(jsonFormatter.format(any(), any(), any())).thenReturn(mockResponse);

        AnalysisResponse response = documentService.analyze(request);

        assertNotNull(response);
        assertEquals("Platform Terms", response.getDocumentType());
    }

    @Test
    void testAnalyzeWithInvalidRequest() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> documentService.analyze(request));
    }

    @Test
    void testAnalyzeWithFailedSanityCheck() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("Valid document text")
                .build();

        when(documentProcessor.processDocument(any(), any())).thenReturn("Valid document text");
        when(documentClassifier.classify(any())).thenReturn(DocumentType.OTHER);
        when(riskFrameworkSelector.selectRiskCategory(any())).thenReturn(RiskCategory.OTHER);

        LLMAnalysisResult mockResult = LLMAnalysisResult.builder()
                .summary("Bad summary")
                .keyPoints(List.of())
                .risks(List.of())
                .recommendations(List.of())
                .confidence(0.85)
                .build();

        when(intentAnalyzer.analyze(any(), any(), any())).thenReturn(mockResult);

        SanityChecker.SanityCheckResult sanityResult = new SanityChecker.SanityCheckResult(false, List.of("Error"));
        when(sanityChecker.check(any())).thenReturn(sanityResult);

        assertThrows(IllegalStateException.class, () -> documentService.analyze(request));
    }

    @Test
    void testAnalyzeTriggersFullPipeline() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("NDA document")
                .fileName("nda.txt")
                .build();

        when(documentProcessor.processDocument(any(), any())).thenReturn("NDA document");
        when(documentClassifier.classify(any())).thenReturn(DocumentType.NDA);
        when(riskFrameworkSelector.selectRiskCategory(DocumentType.NDA)).thenReturn(RiskCategory.DATA_PRIVACY);

        LLMAnalysisResult mockResult = LLMAnalysisResult.builder()
                .summary("NDA Analysis")
                .keyPoints(List.of("Confidentiality"))
                .risks(List.of())
                .recommendations(List.of())
                .confidence(0.9)
                .build();

        when(intentAnalyzer.analyze(any(), any(), any())).thenReturn(mockResult);

        SanityChecker.SanityCheckResult sanityResult = new SanityChecker.SanityCheckResult(true, List.of());
        when(sanityChecker.check(any())).thenReturn(sanityResult);

        AnalysisResponse mockResponse = AnalysisResponse.builder()
                .documentType("NDA")
                .riskCategory("Data Privacy")
                .summary("NDA Analysis")
                .build();

        when(jsonFormatter.format(any(), any(), any())).thenReturn(mockResponse);

        AnalysisResponse response = documentService.analyze(request);

        assertNotNull(response);
        assertEquals("NDA", response.getDocumentType());
    }
}
