package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import com.document.analyzer.domain.RiskCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskFrameworkSelectorTest {

    private RiskFrameworkSelector selector;

    @BeforeEach
    void setUp() {
        selector = new RiskFrameworkSelector();
    }

    @Test
    void testSelectRiskCategoryForPlatformTerms() {
        RiskCategory result = selector.selectRiskCategory(DocumentType.PLATFORM_TERMS);
        assertEquals(RiskCategory.LEGAL_COMPLIANCE, result);
    }

    @Test
    void testSelectRiskCategoryForB2BContract() {
        RiskCategory result = selector.selectRiskCategory(DocumentType.B2B_CONTRACT);
        assertEquals(RiskCategory.CONTRACTUAL, result);
    }

    @Test
    void testSelectRiskCategoryForNDA() {
        RiskCategory result = selector.selectRiskCategory(DocumentType.NDA);
        assertEquals(RiskCategory.DATA_PRIVACY, result);
    }

    @Test
    void testSelectRiskCategoryForEmailChangeOfTerms() {
        RiskCategory result = selector.selectRiskCategory(DocumentType.EMAIL_CHANGE_OF_TERMS);
        assertEquals(RiskCategory.LEGAL_COMPLIANCE, result);
    }

    @Test
    void testSelectRiskCategoryForOther() {
        RiskCategory result = selector.selectRiskCategory(DocumentType.OTHER);
        assertEquals(RiskCategory.OTHER, result);
    }

    @Test
    void testSelectRiskCategoryForNull() {
        RiskCategory result = selector.selectRiskCategory(null);
        assertEquals(RiskCategory.OTHER, result);
    }
}
