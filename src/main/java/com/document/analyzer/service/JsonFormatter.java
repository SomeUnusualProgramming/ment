package com.document.analyzer.service;

import com.document.analyzer.domain.AnalysisResponse;
import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JsonFormatter {

    public AnalysisResponse format(DocumentType documentType, RiskCategory riskCategory, LLMAnalysisResult result) {
        if (result == null) {
            return AnalysisResponse.builder()
                    .documentType(documentType != null ? documentType.getDisplayName() : "Unknown")
                    .riskCategory(riskCategory != null ? riskCategory.getDisplayName() : "Unknown")
                    .summary("No analysis available")
                    .keyPoints(List.of())
                    .risks(List.of())
                    .recommendations(List.of())
                    .metadata(buildMetadata(null))
                    .build();
        }

        List<AnalysisResponse.RiskItem> formattedRisks = formatRisks(result.getRisks());

        return AnalysisResponse.builder()
                .documentType(documentType != null ? documentType.getDisplayName() : "Unknown")
                .riskCategory(riskCategory != null ? riskCategory.getDisplayName() : "Unknown")
                .summary(result.getSummary())
                .keyPoints(result.getKeyPoints() != null ? result.getKeyPoints() : List.of())
                .risks(formattedRisks)
                .recommendations(result.getRecommendations() != null ? result.getRecommendations() : List.of())
                .metadata(buildMetadata(result.getConfidence()))
                .build();
    }

    private List<AnalysisResponse.RiskItem> formatRisks(List<Map<String, String>> risks) {
        if (risks == null || risks.isEmpty()) {
            return List.of();
        }

        return risks.stream()
                .map(this::mapRiskItem)
                .collect(Collectors.toList());
    }

    private AnalysisResponse.RiskItem mapRiskItem(Map<String, String> riskMap) {
        return AnalysisResponse.RiskItem.builder()
                .level(riskMap.getOrDefault("level", "UNKNOWN"))
                .description(riskMap.getOrDefault("description", ""))
                .impact(riskMap.getOrDefault("impact", ""))
                .build();
    }

    private Map<String, Object> buildMetadata(Double confidence) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("analysisTimestamp", Instant.now().toString());
        metadata.put("analyzerVersion", "1.0.0");
        if (confidence != null) {
            metadata.put("confidenceScore", confidence);
        }
        return metadata;
    }
}
