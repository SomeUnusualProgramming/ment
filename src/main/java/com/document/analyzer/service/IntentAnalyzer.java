package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntentAnalyzer {

    private static final String SYSTEM_PROMPT = """
            You are a legal document analyzer. Analyze the provided document and extract:
            1. A concise summary
            2. Key points
            3. Identified risks with level (HIGH, MEDIUM, LOW), description, and impact
            4. Recommendations
            
            Respond in JSON format with fields: summary, keyPoints, risks, recommendations, confidence""";

    public LLMAnalysisResult analyze(String text, DocumentType documentType, RiskCategory riskCategory) {
        if (text == null || text.isBlank()) {
            return LLMAnalysisResult.builder()
                    .summary("No content to analyze")
                    .keyPoints(List.of())
                    .risks(List.of())
                    .recommendations(List.of())
                    .confidence(0.0)
                    .build();
        }

        return callLLMAPI(text, documentType, riskCategory);
    }

    private LLMAnalysisResult callLLMAPI(String text, DocumentType documentType, RiskCategory riskCategory) {
        String prompt = buildPrompt(text, documentType, riskCategory);
        return parseLLMResponse(mockLLMCall(prompt));
    }

    private String buildPrompt(String text, DocumentType documentType, RiskCategory riskCategory) {
        return String.format("""
                %s
                
                Document Type: %s
                Risk Category: %s
                
                Document content:
                %s""", 
                SYSTEM_PROMPT,
                documentType.getDisplayName(),
                riskCategory.getDisplayName(),
                text);
    }

    private String mockLLMCall(String prompt) {
        return """
                {
                  "summary": "Document analysis summary based on content",
                  "keyPoints": ["Key point 1", "Key point 2", "Key point 3"],
                  "risks": [
                    {
                      "level": "HIGH",
                      "description": "Critical risk identified",
                      "impact": "Could lead to legal issues"
                    },
                    {
                      "level": "MEDIUM",
                      "description": "Moderate risk",
                      "impact": "Requires attention"
                    }
                  ],
                  "recommendations": ["Recommendation 1", "Recommendation 2"],
                  "confidence": 0.85
                }""";
    }

    private LLMAnalysisResult parseLLMResponse(String jsonResponse) {
        try {
            Map<String, Object> response = parseJsonManually(jsonResponse);
            
            return LLMAnalysisResult.builder()
                    .summary((String) response.get("summary"))
                    .keyPoints((List<String>) response.get("keyPoints"))
                    .risks((List<Map<String, String>>) response.get("risks"))
                    .recommendations((List<String>) response.get("recommendations"))
                    .confidence(((Number) response.get("confidence")).doubleValue())
                    .build();
        } catch (Exception e) {
            return LLMAnalysisResult.builder()
                    .summary("Analysis failed")
                    .keyPoints(List.of())
                    .risks(List.of())
                    .recommendations(List.of())
                    .confidence(0.0)
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonManually(String json) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("summary", extractJsonString(json, "summary"));
        result.put("keyPoints", extractJsonArray(json, "keyPoints"));
        result.put("risks", extractJsonObjects(json, "risks"));
        result.put("recommendations", extractJsonArray(json, "recommendations"));
        result.put("confidence", extractJsonNumber(json, "confidence"));
        
        return result;
    }

    private String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : "";
    }

    @SuppressWarnings("unchecked")
    private List<String> extractJsonArray(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) {
            String content = m.group(1);
            return Arrays.stream(content.split(","))
                    .map(s -> s.replaceAll("[\"\\s]", ""))
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> extractJsonObjects(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\\[([\\s\\S]*?)\\](?=[,}])";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                String content = m.group(1);
                List<Map<String, String>> objects = new java.util.ArrayList<>();
                
                String[] objectStrings = content.split("\\}\\s*,\\s*\\{");
                for (String objStr : objectStrings) {
                    Map<String, String> obj = new HashMap<>();
                    obj.put("level", extractQuotedValue(objStr, "level"));
                    obj.put("description", extractQuotedValue(objStr, "description"));
                    obj.put("impact", extractQuotedValue(objStr, "impact"));
                    objects.add(obj);
                }
                return objects;
            }
        } catch (Exception e) {
            return List.of();
        }
        return List.of();
    }

    private String extractQuotedValue(String text, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(text);
        return m.find() ? m.group(1) : "";
    }

    private Double extractJsonNumber(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? Double.parseDouble(m.group(1)) : 0.0;
    }
}
