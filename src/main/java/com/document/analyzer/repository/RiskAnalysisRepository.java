package com.document.analyzer.repository;

import com.document.analyzer.entity.RiskAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAnalysisRepository extends JpaRepository<RiskAnalysis, Long> {

    Optional<RiskAnalysis> findByDocumentId(Long documentId);

    List<RiskAnalysis> findByOverallRiskLevel(RiskAnalysis.RiskLevel riskLevel);

    List<RiskAnalysis> findByRiskScoreGreaterThan(Float score);

    List<RiskAnalysis> findByReviewed(Boolean reviewed);

    List<RiskAnalysis> findByFramework(RiskAnalysis.AnalysisFramework framework);

    List<RiskAnalysis> findByReviewedByUserId(Long userId);
}
