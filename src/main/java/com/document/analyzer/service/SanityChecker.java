package com.document.analyzer.service;

import com.document.analyzer.domain.LLMAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SanityChecker {

    private static final List<String> HALLUCINATION_KEYWORDS = List.of(
            "i'm not sure",
            "i apologize",
            "i cannot",
            "i don't know",
            "unclear",
            "unable to determine",
            "insufficient information"
    );

    public SanityCheckResult check(LLMAnalysisResult result) {
        List<String> errors = new ArrayList<>();

        if (result == null) {
            errors.add("Analysis result is null");
            return SanityCheckResult.builder()
                    .valid(false)
                    .errors(errors)
                    .build();
        }

        if (!isValidSummary(result.getSummary())) {
            errors.add("Summary is invalid or missing");
        }

        if (!isValidKeyPoints(result.getKeyPoints())) {
            errors.add("Key points are missing or incomplete");
        }

        if (!isValidRisks(result.getRisks())) {
            errors.add("Risks data is malformed");
        }

        if (!isValidRecommendations(result.getRecommendations())) {
            errors.add("Recommendations are missing or invalid");
        }

        if (!isValidConfidence(result.getConfidence())) {
            errors.add("Confidence score is invalid");
        }

        if (containsHallucinations(result)) {
            errors.add("Potential hallucinations detected in analysis");
        }

        return SanityCheckResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    private boolean isValidSummary(String summary) {
        return summary != null && !summary.isBlank() && summary.length() > 10;
    }

    private boolean isValidKeyPoints(List<String> keyPoints) {
        return keyPoints != null && !keyPoints.isEmpty() && keyPoints.size() <= 10;
    }

    private boolean isValidRisks(List<?> risks) {
        return risks != null && risks.size() >= 0 && risks.size() <= 20;
    }

    private boolean isValidRecommendations(List<String> recommendations) {
        return recommendations != null && recommendations.size() <= 15;
    }

    private boolean isValidConfidence(Double confidence) {
        return confidence != null && confidence >= 0.0 && confidence <= 1.0;
    }

    private boolean containsHallucinations(LLMAnalysisResult result) {
        String textToCheck = (result.getSummary() != null ? result.getSummary() : "") + " " +
                String.join(" ", result.getKeyPoints() != null ? result.getKeyPoints() : List.of());

        String lowerText = textToCheck.toLowerCase();
        return HALLUCINATION_KEYWORDS.stream()
                .anyMatch(lowerText::contains);
    }

    public static class SanityCheckResult {
        private final boolean valid;
        private final List<String> errors;

        public SanityCheckResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public static Builder builder() {
            return new Builder();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public static class Builder {
            private boolean valid;
            private List<String> errors = new ArrayList<>();

            public Builder valid(boolean valid) {
                this.valid = valid;
                return this;
            }

            public Builder errors(List<String> errors) {
                this.errors = errors;
                return this;
            }

            public SanityCheckResult build() {
                return new SanityCheckResult(valid, errors);
            }
        }
    }
}
