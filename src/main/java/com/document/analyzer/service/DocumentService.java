package com.document.analyzer.service;

import com.document.analyzer.domain.AnalysisRequest;
import com.document.analyzer.domain.AnalysisResponse;
import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.LLMAnalysisResult;
import com.document.analyzer.domain.RiskCategory;
import com.document.analyzer.util.DocumentProcessor;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentClassifier documentClassifier;
    private final RiskFrameworkSelector riskFrameworkSelector;
    private final IntentAnalyzer intentAnalyzer;
    private final SanityChecker sanityChecker;
    private final JsonFormatter jsonFormatter;
    private final DocumentProcessor documentProcessor;

    public DocumentService(DocumentClassifier documentClassifier,
                          RiskFrameworkSelector riskFrameworkSelector,
                          IntentAnalyzer intentAnalyzer,
                          SanityChecker sanityChecker,
                          JsonFormatter jsonFormatter,
                          DocumentProcessor documentProcessor) {
        this.documentClassifier = documentClassifier;
        this.riskFrameworkSelector = riskFrameworkSelector;
        this.intentAnalyzer = intentAnalyzer;
        this.sanityChecker = sanityChecker;
        this.jsonFormatter = jsonFormatter;
        this.documentProcessor = documentProcessor;
    }

    public AnalysisResponse analyze(AnalysisRequest request) throws IllegalArgumentException {
        if (!request.isValid()) {
            String error = request.getValidationError();
            throw new IllegalArgumentException(error != null ? error : "Invalid request");
        }

        String text = documentProcessor.processDocument(request.getText(), request.getFileName());

        DocumentType documentType = documentClassifier.classify(text);

        RiskCategory riskCategory = riskFrameworkSelector.selectRiskCategory(documentType);

        LLMAnalysisResult analysisResult = intentAnalyzer.analyze(text, documentType, riskCategory);

        SanityChecker.SanityCheckResult sanityResult = sanityChecker.check(analysisResult);

        if (!sanityResult.isValid()) {
            throw new IllegalStateException("Analysis failed sanity check: " + String.join(", ", sanityResult.getErrors()));
        }

        return jsonFormatter.format(documentType, riskCategory, analysisResult);
    }
}
