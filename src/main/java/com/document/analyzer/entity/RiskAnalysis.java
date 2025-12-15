package com.document.analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RiskAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel overallRiskLevel;

    @Column(nullable = false)
    private Float riskScore;

    @Column(columnDefinition = "TEXT")
    private String identifiedRisks;

    @Column(columnDefinition = "TEXT")
    private String mitigationRecommendations;

    @Column(columnDefinition = "TEXT")
    private String rawAnalysisResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisFramework framework;

    @Column(nullable = false)
    private Boolean reviewed;

    @Column(length = 500)
    private String reviewNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedByUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        reviewed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RiskLevel {
        CRITICAL, HIGH, MEDIUM, LOW, MINIMAL
    }

    public enum AnalysisFramework {
        OWASP, NIST, ISO27001, GDPR, CUSTOM
    }
}
