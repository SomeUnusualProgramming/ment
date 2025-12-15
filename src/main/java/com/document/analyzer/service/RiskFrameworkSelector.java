package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.RiskCategory;
import org.springframework.stereotype.Service;

@Service
public class RiskFrameworkSelector {

    public RiskCategory selectRiskCategory(DocumentType documentType) {
        if (documentType == null) {
            return RiskCategory.OTHER;
        }

        return switch (documentType) {
            case PLATFORM_TERMS -> RiskCategory.LEGAL_COMPLIANCE;
            case B2B_CONTRACT -> RiskCategory.CONTRACTUAL;
            case NDA -> RiskCategory.DATA_PRIVACY;
            case EMAIL_CHANGE_OF_TERMS -> RiskCategory.LEGAL_COMPLIANCE;
            case OTHER -> RiskCategory.OTHER;
        };
    }
}
