package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntentAnalyzerTest {

    private IntentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new IntentAnalyzer();
    }

    @Test
    void testAnalyzeWithValidText() {
        String text = "This is a test document with some content";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result);
        assertNotNull(result.getSummary());
        assertTrue(result.getSummary().length() > 0);
    }

    @Test
    void testAnalyzeWithNullText() {
        LLMAnalysisResult result = analyzer.analyze(null, DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result);
        assertEquals("No content to analyze", result.getSummary());
        assertEquals(0, result.getKeyPoints().size());
        assertEquals(0.0, result.getConfidence());
    }

    @Test
    void testAnalyzeWithEmptyText() {
        LLMAnalysisResult result = analyzer.analyze("", DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result);
        assertEquals("No content to analyze", result.getSummary());
    }

    @Test
    void testAnalyzeWithDocumentType() {
        String text = "Important contract document";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.B2B_CONTRACT, RiskCategory.CONTRACTUAL);

        assertNotNull(result);
        assertNotNull(result.getSummary());
    }

    @Test
    void testAnalyzeReturnsKeyPoints() {
        String text = "Test document";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result.getKeyPoints());
        assertTrue(result.getKeyPoints().size() > 0);
    }

    @Test
    void testAnalyzeReturnsRisks() {
        String text = "Test document with potential risks";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.B2B_CONTRACT, RiskCategory.CONTRACTUAL);

        assertNotNull(result.getRisks());
    }

    @Test
    void testAnalyzeReturnsRecommendations() {
        String text = "Test document";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result.getRecommendations());
    }

    @Test
    void testAnalyzeReturnsConfidence() {
        String text = "Test document";
        LLMAnalysisResult result = analyzer.analyze(text, DocumentType.OTHER, RiskCategory.OTHER);

        assertNotNull(result.getConfidence());
        assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0);
    }
}
