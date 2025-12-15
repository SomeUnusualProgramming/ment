package com.document.analyzer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResponse {
    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("riskCategory")
    private String riskCategory;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("keyPoints")
    private List<String> keyPoints;

    @JsonProperty("risks")
    private List<RiskItem> risks;

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RiskItem {
        @JsonProperty("level")
        private String level;

        @JsonProperty("description")
        private String description;

        @JsonProperty("impact")
        private String impact;
    }
}
