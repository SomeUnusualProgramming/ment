package com.document.analyzer.service;

import com.document.analyzer.domain.AnalysisResponse;
import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFormatterTest {

    private JsonFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new JsonFormatter();
    }

    @Test
    void testFormatWithValidResult() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("Analysis summary")
                .keyPoints(List.of("Point 1", "Point 2"))
                .risks(List.of(
                        Map.of("level", "HIGH", "description", "Risk 1", "impact", "High impact"),
                        Map.of("level", "MEDIUM", "description", "Risk 2", "impact", "Medium impact")
                ))
                .recommendations(List.of("Recommendation 1"))
                .confidence(0.85)
                .build();

        AnalysisResponse response = formatter.format(DocumentType.B2B_CONTRACT, RiskCategory.CONTRACTUAL, result);

        assertNotNull(response);
        assertEquals("B2B Contract", response.getDocumentType());
        assertEquals("Contractual", response.getRiskCategory());
        assertEquals("Analysis summary", response.getSummary());
        assertEquals(2, response.getKeyPoints().size());
    }

    @Test
    void testFormatWithNullResult() {
        AnalysisResponse response = formatter.format(DocumentType.OTHER, RiskCategory.OTHER, null);

        assertNotNull(response);
        assertEquals("Other", response.getDocumentType());
        assertEquals("Other", response.getRiskCategory());
        assertEquals("No analysis available", response.getSummary());
    }

    @Test
    void testFormatMapsRisksCorrectly() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("Analysis summary")
                .keyPoints(List.of("Point 1"))
                .risks(List.of(
                        Map.of("level", "HIGH", "description", "Critical issue", "impact", "Severe")
                ))
                .recommendations(List.of())
                .confidence(0.9)
                .build();

        AnalysisResponse response = formatter.format(DocumentType.NDA, RiskCategory.DATA_PRIVACY, result);

        assertEquals(1, response.getRisks().size());
        assertEquals("HIGH", response.getRisks().get(0).getLevel());
        assertEquals("Critical issue", response.getRisks().get(0).getDescription());
        assertEquals("Severe", response.getRisks().get(0).getImpact());
    }

    @Test
    void testFormatIncludesMetadata() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("Analysis summary")
                .keyPoints(List.of("Point 1"))
                .risks(List.of())
                .recommendations(List.of())
                .confidence(0.75)
                .build();

        AnalysisResponse response = formatter.format(DocumentType.PLATFORM_TERMS, RiskCategory.LEGAL_COMPLIANCE, result);

        assertNotNull(response.getMetadata());
        assertTrue(response.getMetadata().containsKey("analysisTimestamp"));
        assertTrue(response.getMetadata().containsKey("analyzerVersion"));
        assertTrue(response.getMetadata().containsKey("confidenceScore"));
        assertEquals(0.75, response.getMetadata().get("confidenceScore"));
    }

    @Test
    void testFormatWithEmptyLists() {
        LLMAnalysisResult result = LLMAnalysisResult.builder()
                .summary("Summary")
                .keyPoints(List.of())
                .risks(List.of())
                .recommendations(List.of())
                .confidence(0.5)
                .build();

        AnalysisResponse response = formatter.format(DocumentType.OTHER, RiskCategory.OTHER, result);

        assertNotNull(response);
        assertEquals(0, response.getKeyPoints().size());
        assertEquals(0, response.getRisks().size());
        assertEquals(0, response.getRecommendations().size());
    }
}
