package com.document.analyzer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalysisRequestTest {

    @Test
    void testValidRequest() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("This is a valid document with some content")
                .fileName("test.txt")
                .build();

        assertTrue(request.isValid());
        assertTrue(request.getValidationError() == null);
    }

    @Test
    void testRequestWithNullText() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text(null)
                .fileName("test.txt")
                .build();

        assertFalse(request.isValid());
        assertTrue(request.getValidationError().contains("required"));
    }

    @Test
    void testRequestWithEmptyText() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("")
                .fileName("test.txt")
                .build();

        assertFalse(request.isValid());
    }

    @Test
    void testRequestWithBlankText() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("   ")
                .fileName("test.txt")
                .build();

        assertFalse(request.isValid());
    }

    @Test
    void testRequestExceedsMaxLength() {
        String longText = "a".repeat(50001);
        AnalysisRequest request = AnalysisRequest.builder()
                .text(longText)
                .fileName("test.txt")
                .build();

        assertFalse(request.isValid());
        assertTrue(request.getValidationError().contains("exceeds maximum length"));
    }

    @Test
    void testRequestAtMaxLength() {
        String maxText = "a".repeat(50000);
        AnalysisRequest request = AnalysisRequest.builder()
                .text(maxText)
                .fileName("test.txt")
                .build();

        assertTrue(request.isValid());
    }

    @Test
    void testRequestWithUTF8Text() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("This is valid UTF-8: áéíóú ñ ü")
                .fileName("test.txt")
                .build();

        assertTrue(request.isValid());
    }

    @Test
    void testRequestWithBinaryContent() {
        String textWithBinary = "Valid text" + (char)0x01 + "with binary";
        AnalysisRequest request = AnalysisRequest.builder()
                .text(textWithBinary)
                .fileName("test.txt")
                .build();

        assertFalse(request.isValid());
        assertTrue(request.getValidationError().contains("binary content"));
    }

    @Test
    void testRequestWithoutFileName() {
        AnalysisRequest request = AnalysisRequest.builder()
                .text("This is a valid document")
                .build();

        assertTrue(request.isValid());
    }
}
