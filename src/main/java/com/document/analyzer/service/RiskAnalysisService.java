package com.document.analyzer.service;

import com.document.analyzer.entity.Document;
import com.document.analyzer.entity.RiskAnalysis;
import com.document.analyzer.entity.User;
import com.document.analyzer.repository.DocumentRepository;
import com.document.analyzer.repository.RiskAnalysisRepository;
import com.document.analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RiskAnalysisService {

    private final RiskAnalysisRepository riskAnalysisRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public RiskAnalysis analyzeDocumentRisk(Long documentId, RiskAnalysis.AnalysisFramework framework) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        float riskScore = calculateRiskScore(document);
        RiskAnalysis.RiskLevel riskLevel = determineRiskLevel(riskScore);
        String identifiedRisks = generateIdentifiedRisks(document, riskLevel);
        String mitigationRecommendations = generateMitigationRecommendations(riskLevel);

        RiskAnalysis analysis = RiskAnalysis.builder()
                .document(document)
                .overallRiskLevel(riskLevel)
                .riskScore(riskScore)
                .identifiedRisks(identifiedRisks)
                .mitigationRecommendations(mitigationRecommendations)
                .rawAnalysisResult(String.format("Risk Analysis using %s framework: Score %.2f, Level: %s", framework, riskScore, riskLevel))
                .framework(framework)
                .reviewed(false)
                .build();

        return riskAnalysisRepository.save(analysis);
    }

    public Optional<RiskAnalysis> getAnalysisForDocument(Long documentId) {
        return riskAnalysisRepository.findByDocumentId(documentId);
    }

    public List<RiskAnalysis> getAnalysisByRiskLevel(RiskAnalysis.RiskLevel level) {
        return riskAnalysisRepository.findByOverallRiskLevel(level);
    }

    public List<RiskAnalysis> getHighRiskAnalyses(Float minScore) {
        return riskAnalysisRepository.findByRiskScoreGreaterThan(minScore);
    }

    public List<RiskAnalysis> getUnreviewedAnalyses() {
        return riskAnalysisRepository.findByReviewed(false);
    }

    public RiskAnalysis reviewAnalysis(Long analysisId, Long reviewerUserId, String reviewNotes) {
        User reviewer = userRepository.findById(reviewerUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return riskAnalysisRepository.findById(analysisId)
                .map(analysis -> {
                    analysis.setReviewed(true);
                    analysis.setReviewNotes(reviewNotes);
                    analysis.setReviewedByUser(reviewer);
                    return riskAnalysisRepository.save(analysis);
                })
                .orElseThrow(() -> new RuntimeException("Analysis not found"));
    }

    public RiskAnalysis updateRiskLevel(Long analysisId, RiskAnalysis.RiskLevel newLevel) {
        return riskAnalysisRepository.findById(analysisId)
                .map(analysis -> {
                    analysis.setOverallRiskLevel(newLevel);
                    return riskAnalysisRepository.save(analysis);
                })
                .orElseThrow(() -> new RuntimeException("Analysis not found"));
    }

    public List<RiskAnalysis> getAnalysesByFramework(RiskAnalysis.AnalysisFramework framework) {
        return riskAnalysisRepository.findByFramework(framework);
    }

    public List<RiskAnalysis> getAnalysesReviewedBy(Long userId) {
        return riskAnalysisRepository.findByReviewedByUserId(userId);
    }

    private float calculateRiskScore(Document document) {
        String fileName = document.getFileName() != null ? document.getFileName().toLowerCase() : "";
        String content = document.getExtractedText() != null ? document.getExtractedText().toLowerCase() : "";
        
        float riskScore = 0.05f;
        
        if (content.contains("password") || content.contains("api key") || content.contains("api_key") || 
            content.contains("secret") || content.contains("token") || content.contains("credential")) {
            riskScore += 0.35f;
        }
        
        if (content.contains("personal data") || content.contains("pii") || content.contains("ssn") || 
            content.contains("social security") || content.contains("email address")) {
            riskScore += 0.30f;
        }
        
        if (content.contains("credit card") || content.contains("bank account") || 
            content.contains("financial") || content.contains("payment")) {
            riskScore += 0.35f;
        }
        
        if (content.contains("medical") || content.contains("health") || content.contains("diagnosis") || 
            content.contains("patient") || content.contains("prescription")) {
            riskScore += 0.30f;
        }
        
        if (content.contains("confidential") || content.contains("restricted") || content.contains("proprietary")) {
            riskScore += 0.25f;
        }
        
        if (content.contains("nda") || content.contains("non-disclosure") || content.contains("agreement") || 
            content.contains("contract")) {
            riskScore += 0.20f;
        }
        
        if (fileName.endsWith(".json") || fileName.endsWith(".csv") || 
            fileName.endsWith(".xlsx") || fileName.endsWith(".xml")) {
            riskScore += 0.15f;
        }
        
        if (content.length() < 100) {
            riskScore -= 0.05f;
        }
        
        return Math.min(1.0f, Math.max(0.05f, riskScore));
    }

    private RiskAnalysis.RiskLevel determineRiskLevel(float riskScore) {
        if (riskScore >= 0.8f) {
            return RiskAnalysis.RiskLevel.CRITICAL;
        } else if (riskScore >= 0.6f) {
            return RiskAnalysis.RiskLevel.HIGH;
        } else if (riskScore >= 0.4f) {
            return RiskAnalysis.RiskLevel.MEDIUM;
        } else if (riskScore >= 0.2f) {
            return RiskAnalysis.RiskLevel.LOW;
        } else {
            return RiskAnalysis.RiskLevel.MINIMAL;
        }
    }

    private String generateIdentifiedRisks(Document document, RiskAnalysis.RiskLevel level) {
        StringBuilder risks = new StringBuilder();
        String fileName = document.getFileName() != null ? document.getFileName().toLowerCase() : "";
        String content = document.getExtractedText() != null ? document.getExtractedText().toLowerCase() : "";
        
        boolean hasActualRisks = false;
        
        if (content.contains("password") || content.contains("api key") || content.contains("api_key") || 
            content.contains("secret") || content.contains("token") || content.contains("credential")) {
            risks.append("- Potential credentials, API keys, or security tokens detected in document\n");
            hasActualRisks = true;
        }
        
        if (content.contains("confidential") || content.contains("restricted") || content.contains("proprietary")) {
            risks.append("- Document contains proprietary or confidential information\n");
            hasActualRisks = true;
        }
        
        if (content.contains("personal data") || content.contains("pii") || content.contains("ssn") || 
            content.contains("social security") || content.contains("email address") || content.contains("phone number")) {
            risks.append("- Personally Identifiable Information (PII) or personal data detected\n");
            hasActualRisks = true;
        }
        
        if (content.contains("credit card") || content.contains("bank") || content.contains("account number") ||
            content.contains("financial") || content.contains("payment")) {
            risks.append("- Financial or payment information may be present\n");
            hasActualRisks = true;
        }
        
        if (content.contains("medical") || content.contains("health") || content.contains("diagnosis") || 
            content.contains("treatment") || content.contains("patient") || content.contains("prescription")) {
            risks.append("- Protected Health Information (PHI) or medical records detected\n");
            hasActualRisks = true;
        }
        
        if (content.contains("nda") || content.contains("non-disclosure") || content.contains("agreement") || 
            content.contains("contract") || content.contains("legal")) {
            risks.append("- Legal agreements or confidentiality clauses present\n");
            hasActualRisks = true;
        }
        
        if (fileName.endsWith(".json") || fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xml")) {
            risks.append("- Structured data format detected - verify access controls on data repository\n");
            hasActualRisks = true;
        }
        
        if (level == RiskAnalysis.RiskLevel.CRITICAL) {
            risks.append("- CRITICAL: Immediate remediation action required\n");
            hasActualRisks = true;
        } else if (level == RiskAnalysis.RiskLevel.HIGH) {
            risks.append("- HIGH: Elevated risk requires security review\n");
            hasActualRisks = true;
        }
        
        if (!hasActualRisks) {
            risks.append("- Document analyzed with standard risk assessment\n");
            risks.append("- No critical security indicators identified\n");
            risks.append("- Continue regular security monitoring\n");
        }
        
        return risks.toString();
    }

    private String generateMitigationRecommendations(RiskAnalysis.RiskLevel level) {
        if (level == RiskAnalysis.RiskLevel.CRITICAL) {
            return "Immediate action required:\n- Quarantine document from unsecured access\n- Conduct emergency security audit\n- Implement strict access controls (need-to-know basis)\n- Notify security team and relevant stakeholders\n- Consider data classification and encryption";
        } else if (level == RiskAnalysis.RiskLevel.HIGH) {
            return "Urgent recommendations:\n- Implement role-based access controls (RBAC)\n- Enable document access logging\n- Conduct compliance review\n- Schedule security assessment\n- Implement encryption for storage and transmission";
        } else if (level == RiskAnalysis.RiskLevel.MEDIUM) {
            return "Recommended actions:\n- Ensure appropriate access controls are in place\n- Monitor document access patterns\n- Document data classification\n- Implement version control and audit trails\n- Review and update access documentation";
        } else {
            return "Standard best practices:\n- Maintain current security measures\n- Continue regular monitoring\n- Keep data access controls current\n- Document any sensitive data properly\n- Train staff on data handling best practices";
        }
    }
}
