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
public class LLMAnalysisResult {
    @JsonProperty("summary")
    private String summary;

    @JsonProperty("keyPoints")
    private List<String> keyPoints;

    @JsonProperty("risks")
    private List<Map<String, String>> risks;

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty("confidence")
    private Double confidence;
}
