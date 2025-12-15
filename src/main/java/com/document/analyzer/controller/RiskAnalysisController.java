package com.document.analyzer.controller;

import com.document.analyzer.entity.RiskAnalysis;
import com.document.analyzer.service.RiskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/risk-analysis")
@RequiredArgsConstructor
public class RiskAnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    @PostMapping("/analyze/{documentId}")
    public ResponseEntity<RiskAnalysis> analyzeDocument(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "OWASP") RiskAnalysis.AnalysisFramework framework) {
        RiskAnalysis analysis = riskAnalysisService.analyzeDocumentRisk(documentId, framework);
        return ResponseEntity.status(HttpStatus.CREATED).body(analysis);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<RiskAnalysis> getAnalysis(@PathVariable Long documentId) {
        Optional<RiskAnalysis> analysis = riskAnalysisService.getAnalysisForDocument(documentId);
        return analysis.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/level/{riskLevel}")
    public ResponseEntity<List<RiskAnalysis>> getByRiskLevel(@PathVariable RiskAnalysis.RiskLevel riskLevel) {
        List<RiskAnalysis> analyses = riskAnalysisService.getAnalysisByRiskLevel(riskLevel);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/high-risk/{minScore}")
    public ResponseEntity<List<RiskAnalysis>> getHighRisk(@PathVariable Float minScore) {
        List<RiskAnalysis> analyses = riskAnalysisService.getHighRiskAnalyses(minScore);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/unreviewed")
    public ResponseEntity<List<RiskAnalysis>> getUnreviewed() {
        List<RiskAnalysis> analyses = riskAnalysisService.getUnreviewedAnalyses();
        return ResponseEntity.ok(analyses);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<RiskAnalysis> reviewAnalysis(
            @PathVariable Long id,
            @RequestParam Long reviewerUserId,
            @RequestParam(required = false) String reviewNotes) {
        RiskAnalysis reviewed = riskAnalysisService.reviewAnalysis(id, reviewerUserId, reviewNotes);
        return ResponseEntity.ok(reviewed);
    }

    @PutMapping("/{id}/risk-level")
    public ResponseEntity<RiskAnalysis> updateRiskLevel(
            @PathVariable Long id,
            @RequestParam RiskAnalysis.RiskLevel newLevel) {
        RiskAnalysis updated = riskAnalysisService.updateRiskLevel(id, newLevel);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/framework/{framework}")
    public ResponseEntity<List<RiskAnalysis>> getByFramework(@PathVariable RiskAnalysis.AnalysisFramework framework) {
        List<RiskAnalysis> analyses = riskAnalysisService.getAnalysesByFramework(framework);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/reviewed-by/{userId}")
    public ResponseEntity<List<RiskAnalysis>> getReviewedBy(@PathVariable Long userId) {
        List<RiskAnalysis> analyses = riskAnalysisService.getAnalysesReviewedBy(userId);
        return ResponseEntity.ok(analyses);
    }
}
