package com.document.analyzer.service;

import com.document.analyzer.domain.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentClassifierTest {

    private DocumentClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new DocumentClassifier();
    }

    @Test
    void testClassifyTermsOfService() {
        String text = "Terms of Service and User Agreement";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.PLATFORM_TERMS, result);
    }

    @Test
    void testClassifyB2BContract() {
        String text = "This Service Level Agreement defines the deliverables between vendor and supplier";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.B2B_CONTRACT, result);
    }

    @Test
    void testClassifyNDA() {
        String text = "Non-Disclosure Agreement protecting confidential and proprietary information";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.NDA, result);
    }

    @Test
    void testClassifyEmailChangeOfTerms() {
        String text = "Dear customer, we are writing to inform you of changes to our policy effective date January 1st";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.EMAIL_CHANGE_OF_TERMS, result);
    }

    @Test
    void testClassifyOther() {
        String text = "This is a generic document with no specific keywords";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.OTHER, result);
    }

    @Test
    void testClassifyNullText() {
        DocumentType result = classifier.classify(null);
        assertEquals(DocumentType.OTHER, result);
    }

    @Test
    void testClassifyEmptyText() {
        DocumentType result = classifier.classify("");
        assertEquals(DocumentType.OTHER, result);
    }

    @Test
    void testClassifyCaseInsensitive() {
        String text = "TERMS OF SERVICE IN UPPERCASE";
        DocumentType result = classifier.classify(text);
        assertEquals(DocumentType.PLATFORM_TERMS, result);
    }
}
