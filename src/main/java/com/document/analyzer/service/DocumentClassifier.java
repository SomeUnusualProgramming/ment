package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import org.springframework.stereotype.Service;

@Service
public class DocumentClassifier {

    public DocumentType classify(String text) {
        if (text == null || text.isBlank()) {
            return DocumentType.OTHER;
        }

        String lowerText = text.toLowerCase();

        if (matchesNDA(lowerText)) {
            return DocumentType.NDA;
        }

        if (matchesEmailChangeOfTerms(lowerText)) {
            return DocumentType.EMAIL_CHANGE_OF_TERMS;
        }

        if (matchesTermsOfService(lowerText)) {
            return DocumentType.PLATFORM_TERMS;
        }

        if (matchesB2BContract(lowerText)) {
            return DocumentType.B2B_CONTRACT;
        }

        return DocumentType.OTHER;
    }

    private boolean matchesTermsOfService(String text) {
        return text.contains("terms of service") || 
               text.contains("terms and conditions") ||
               text.contains("user agreement") ||
               text.contains("service agreement") ||
               text.contains("platform terms") ||
               text.contains("acceptable use");
    }

    private boolean matchesB2BContract(String text) {
        return text.contains("contract") ||
               text.contains("agreement") ||
               text.contains("vendor") ||
               text.contains("supplier") ||
               text.contains("service level agreement") ||
               text.contains("sla") ||
               text.contains("deliverables");
    }

    private boolean matchesNDA(String text) {
        return text.contains("nda") ||
               text.contains("non-disclosure") ||
               text.contains("confidential") ||
               text.contains("proprietary information") ||
               text.contains("trade secret") ||
               text.contains("confidentiality agreement");
    }

    private boolean matchesEmailChangeOfTerms(String text) {
        return text.contains("changes to") ||
               text.contains("policy update") ||
               text.contains("effective date") ||
               text.contains("subject: changes") ||
               text.contains("from:") ||
               text.contains("dear customer");
    }
}
