package com.document.analyzer.controller;

import com.document.analyzer.domain.AnalysisRequest;
import com.document.analyzer.domain.AnalysisResponse;
import com.document.analyzer.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void testAnalyzeEndpointWithValidRequest() throws Exception {
        AnalysisResponse mockResponse = AnalysisResponse.builder()
                .documentType("B2B Contract")
                .riskCategory("Contractual")
                .summary("Analysis summary")
                .keyPoints(List.of("Key point 1"))
                .recommendations(List.of("Recommendation 1"))
                .build();

        when(documentService.analyze(any(AnalysisRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Valid document content\", \"fileName\": \"test.txt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("B2B Contract"))
                .andExpect(jsonPath("$.riskCategory").value("Contractual"));
    }

    @Test
    void testAnalyzeEndpointWithInvalidRequest() throws Exception {
        when(documentService.analyze(any(AnalysisRequest.class)))
                .thenThrow(new IllegalArgumentException("Text field is required"));

        mockMvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"\", \"fileName\": \"test.txt\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void testAnalyzeEndpointWithAnalysisError() throws Exception {
        when(documentService.analyze(any(AnalysisRequest.class)))
                .thenThrow(new IllegalStateException("Analysis failed sanity check"));

        mockMvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Valid document content\", \"fileName\": \"test.txt\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Analysis Error"));
    }

    @Test
    void testAnalyzeEndpointWithGeneralException() throws Exception {
        when(documentService.analyze(any(AnalysisRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Valid document content\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @Test
    void testAnalyzeEndpointReturnsFullResponse() throws Exception {
        AnalysisResponse mockResponse = AnalysisResponse.builder()
                .documentType("NDA")
                .riskCategory("Data Privacy")
                .summary("This is a comprehensive NDA analysis")
                .keyPoints(List.of("Confidentiality clause", "Term and termination"))
                .risks(List.of(
                        AnalysisResponse.RiskItem.builder()
                                .level("HIGH")
                                .description("Broad scope")
                                .impact("Significant restrictions")
                                .build()
                ))
                .recommendations(List.of("Review duration limits", "Add exception clauses"))
                .build();

        when(documentService.analyze(any(AnalysisRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"NDA document content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("NDA"))
                .andExpect(jsonPath("$.riskCategory").value("Data Privacy"))
                .andExpect(jsonPath("$.summary").value("This is a comprehensive NDA analysis"))
                .andExpect(jsonPath("$.keyPoints[0]").value("Confidentiality clause"))
                .andExpect(jsonPath("$.risks[0].level").value("HIGH"));
    }
}
