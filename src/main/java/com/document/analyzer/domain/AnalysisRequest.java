package com.document.analyzer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisRequest {
    @JsonProperty("text")
    private String text;

    @JsonProperty("fileName")
    private String fileName;

    public boolean isValid() {
        if (text == null || text.isBlank()) {
            return false;
        }

        if (text.length() > 50000) {
            return false;
        }

        try {
            text.getBytes("UTF-8");
        } catch (Exception e) {
            return false;
        }

        return !isBinaryContent(text);
    }

    private boolean isBinaryContent(String text) {
        for (char c : text.toCharArray()) {
            if (c < 32 && c != '\n' && c != '\r' && c != '\t') {
                return true;
            }
        }
        return false;
    }

    public String getValidationError() {
        if (text == null || text.isBlank()) {
            return "Text field is required";
        }

        if (text.length() > 50000) {
            return "Text exceeds maximum length of 50000 characters";
        }

        try {
            text.getBytes("UTF-8");
        } catch (Exception e) {
            return "Text contains invalid UTF-8 characters";
        }

        if (isBinaryContent(text)) {
            return "Text contains binary content";
        }

        return null;
    }
}
