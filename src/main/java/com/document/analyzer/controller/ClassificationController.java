package com.document.analyzer.controller;

import com.document.analyzer.entity.Classification;
import com.document.analyzer.service.ClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classifications")
@RequiredArgsConstructor
public class ClassificationController {

    private final ClassificationService classificationService;

    @PostMapping("/classify/{documentId}")
    public ResponseEntity<Classification> classifyDocument(@PathVariable Long documentId) {
        Classification classification = classificationService.classifyDocument(documentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(classification);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Classification> getLatestClassification(@PathVariable Long documentId) {
        Optional<Classification> classification = classificationService.getLatestClassification(documentId);
        return classification.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Classification>> getByCategory(@PathVariable Classification.DocumentCategory category) {
        List<Classification> classifications = classificationService.getClassificationsByCategory(category);
        return ResponseEntity.ok(classifications);
    }

    @GetMapping("/high-confidence/{minConfidence}")
    public ResponseEntity<List<Classification>> getHighConfidence(@PathVariable Float minConfidence) {
        List<Classification> classifications = classificationService.getHighConfidenceClassifications(minConfidence);
        return ResponseEntity.ok(classifications);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classification> updateClassification(
            @PathVariable Long id,
            @RequestParam Classification.DocumentCategory newCategory) {
        Classification updated = classificationService.updateClassification(id, newCategory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassification(@PathVariable Long id) {
        classificationService.deleteClassification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateClassification(@PathVariable Long id) {
        boolean isAccurate = classificationService.isClassificationAccurate(id);
        return ResponseEntity.ok(isAccurate);
    }
}
