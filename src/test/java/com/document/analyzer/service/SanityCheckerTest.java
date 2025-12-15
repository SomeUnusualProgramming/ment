package com.document.analyzer.service;

import com.document.analyzer.domain.LLMAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanityCheckerTest {

    private SanityChecker checker;

    @BeforeEach
    void setUp() {
        checker = new SanityChecker();
    }

    @Test
    void testCheckWithValidResult() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis of the document")
                .keyPoints(List.of("Point 1", "Point 2", "Point 3"))
                .risks(List.of(
                        Map.of("level", "HIGH", "description", "Risk 1", "impact", "High impact"),
                        Map.of("level", "MEDIUM", "description", "Risk 2", "impact", "Medium impact")
                ))
                .recommendations(List.of("Rec 1", "Rec 2"))
                .confidence(0.85)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertTrue(checkResult.isValid());
    }

    @Test
    void testCheckWithNullResult() {
        SanityChecker.SanityCheckResult checkResult = checker.check(null);
        assertFalse(checkResult.isValid());
        assertTrue(checkResult.getErrors().contains("Analysis result is null"));
    }

    @Test
    void testCheckWithInvalidSummary() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("Short")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .confidence(0.85)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
    }

    @Test
    void testCheckWithMissingKeyPoints() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis of the document")
                .keyPoints(List.of())
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .confidence(0.85)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
    }

    @Test
    void testCheckWithInvalidConfidenceHigh() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis of the document")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .confidence(1.5)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
    }

    @Test
    void testCheckWithInvalidConfidenceLow() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis of the document")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .confidence(-0.1)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
    }

    @Test
    void testCheckDetectsHallucinations() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis. I'm not sure about the details.")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(List.of())
                .recommendations(List.of("Rec 1"))
                .confidence(0.85)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
        assertTrue(checkResult.getErrors().stream()
                .anyMatch(e -> e.contains("hallucination")));
    }

    @Test
    void testCheckWithTooManyRisks() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("This is a comprehensive analysis of the document")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(createManyRisks(25))
                .recommendations(List.of("Rec 1"))
                .confidence(0.85)
                .build();

        SanityChecker.SanityCheckResult checkResult = checker.check(result);
        assertFalse(checkResult.isValid());
    }

    private List<Map<String, String>> createManyRisks(int count) {
        List<Map<String, String>> risks = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> risk = new HashMap<>();
            risk.put("level", "MEDIUM");
            risk.put("description", "Risk " + i);
            risk.put("impact", "Impact " + i);
            risks.add(risk);
        }
        return risks;
    }
}
