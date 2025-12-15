package com.document.analyzer.service;

import com.document.analyzer.entity.Classification;
import com.document.analyzer.entity.Document;
import com.document.analyzer.repository.ClassificationRepository;
import com.document.analyzer.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final DocumentRepository documentRepository;

    public Classification classifyDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Classification.DocumentCategory category = inferCategoryFromDocument(document);
        float confidence = generateConfidenceScore(document.getFileName());
        String reason = generateClassificationReason(category, document.getFileName());

        Classification classification = Classification.builder()
                .document(document)
                .category(category)
                .confidence(confidence)
                .classificationReason(reason)
                .rawClassificationResult(String.format("Classification: %s with confidence %f", category, confidence))
                .build();

        return classificationRepository.save(classification);
    }

    public Optional<Classification> getLatestClassification(Long documentId) {
        return classificationRepository.findByDocumentId(documentId);
    }

    public List<Classification> getClassificationsByCategory(Classification.DocumentCategory category) {
        return classificationRepository.findByCategory(category);
    }

    public List<Classification> getHighConfidenceClassifications(Float minConfidence) {
        return classificationRepository.findByConfidenceGreaterThan(minConfidence);
    }

    public Classification updateClassification(Long classificationId, Classification.DocumentCategory newCategory) {
        return classificationRepository.findById(classificationId)
                .map(classification -> {
                    classification.setCategory(newCategory);
                    classification.setClassificationReason("TODO: Manual update reason");
                    return classificationRepository.save(classification);
                })
                .orElseThrow(() -> new RuntimeException("Classification not found"));
    }

    public void deleteClassification(Long id) {
        classificationRepository.deleteById(id);
    }

    public boolean isClassificationAccurate(Long classificationId) {
        return classificationRepository.findById(classificationId)
                .map(c -> c.getConfidence() >= 0.80f)
                .orElse(false);
    }

    private Classification.DocumentCategory inferCategoryFromDocument(Document document) {
        String fileName = document.getFileName() != null ? document.getFileName().toLowerCase() : "";
        String extractedText = document.getExtractedText() != null ? document.getExtractedText().toLowerCase() : "";
        
        if (isJsonFile(fileName, extractedText)) {
            return Classification.DocumentCategory.OTHER;
        }
        if (fileName.contains("contract") || extractedText.contains("contract") || extractedText.contains("agreement")) {
            return Classification.DocumentCategory.CONTRACT;
        }
        if (fileName.contains("invoice") || extractedText.contains("invoice") || extractedText.contains("bill")) {
            return Classification.DocumentCategory.INVOICE;
        }
        if (fileName.contains("report") || extractedText.contains("report") || extractedText.contains("analysis")) {
            return Classification.DocumentCategory.REPORT;
        }
        if (fileName.contains("policy") || extractedText.contains("policy") || extractedText.contains("guideline")) {
            return Classification.DocumentCategory.POLICY;
        }
        if (fileName.contains("form") || extractedText.contains("form") || extractedText.contains("application")) {
            return Classification.DocumentCategory.FORM;
        }
        if (extractedText.contains("agreement") || fileName.contains("agreement")) {
            return Classification.DocumentCategory.AGREEMENT;
        }
        return Classification.DocumentCategory.OTHER;
    }

    private boolean isJsonFile(String fileName, String extractedText) {
        if (fileName.endsWith(".json")) {
            return true;
        }
        if (extractedText != null && extractedText.trim().startsWith("{") && extractedText.contains(":")) {
            return true;
        }
        return false;
    }

    private float generateConfidenceScore(String fileName) {
        if (fileName != null && fileName.toLowerCase().endsWith(".json")) {
            return 0.95f;
        }
        if (fileName == null || fileName.isBlank()) {
            return 0.45f;
        }
        
        int matchScore = 0;
        String lower = fileName.toLowerCase();
        if (lower.contains("contract")) matchScore += 30;
        if (lower.contains("invoice")) matchScore += 30;
        if (lower.contains("report")) matchScore += 25;
        if (lower.contains("policy")) matchScore += 25;
        if (lower.contains("form")) matchScore += 20;
        if (lower.contains("agreement")) matchScore += 30;
        
        return Math.min(1.0f, 0.50f + (matchScore / 200.0f));
    }

    private String generateClassificationReason(Classification.DocumentCategory category, String fileName) {
        if (category == Classification.DocumentCategory.OTHER && fileName != null && fileName.toLowerCase().endsWith(".json")) {
            return "Document '" + fileName + "' is a JSON data file. Detected as structured data format.";
        }
        return String.format("Document '%s' has been classified as %s based on content analysis and document structure.",
                fileName, category.toString());
    }
}
