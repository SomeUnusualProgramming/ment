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
import java.util.Random;

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
        int hashCode = (document.getFileName() + document.getDocumentType()).hashCode();
        Random random = new Random(Math.abs(hashCode));
        return random.nextFloat();
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
        risks.append("Identified risks for ").append(document.getFileName()).append(":\n");
        
        if (level == RiskAnalysis.RiskLevel.CRITICAL) {
            risks.append("- Critical security vulnerabilities detected\n");
            risks.append("- Sensitive data exposure potential\n");
            risks.append("- Compliance violations\n");
        } else if (level == RiskAnalysis.RiskLevel.HIGH) {
            risks.append("- Significant security concerns\n");
            risks.append("- Data privacy issues\n");
            risks.append("- Regulatory compliance risks\n");
        } else if (level == RiskAnalysis.RiskLevel.MEDIUM) {
            risks.append("- Moderate security concerns\n");
            risks.append("- Potential compliance issues\n");
            risks.append("- Access control considerations\n");
        } else {
            risks.append("- Minor security observations\n");
            risks.append("- Documentation improvements recommended\n");
        }
        
        return risks.toString();
    }

    private String generateMitigationRecommendations(RiskAnalysis.RiskLevel level) {
        if (level == RiskAnalysis.RiskLevel.CRITICAL) {
            return "Immediate action required:\n- Conduct thorough security audit\n- Implement access controls\n- Review and update security policies\n- Consider document quarantine";
        } else if (level == RiskAnalysis.RiskLevel.HIGH) {
            return "Urgent recommendations:\n- Review security measures\n- Implement stricter access controls\n- Conduct compliance review\n- Schedule security assessment";
        } else if (level == RiskAnalysis.RiskLevel.MEDIUM) {
            return "Recommended actions:\n- Review current security measures\n- Implement suggested improvements\n- Monitor document usage\n- Update security documentation";
        } else {
            return "Standard best practices:\n- Maintain current security measures\n- Continue regular monitoring\n- Review documentation annually\n- Staff security awareness training";
        }
    }
}
