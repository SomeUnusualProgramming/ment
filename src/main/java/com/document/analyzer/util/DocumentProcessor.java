package com.document.analyzer.util;

import org.springframework.stereotype.Component;

@Component
public class DocumentProcessor {

    public String processDocument(String text, String fileName) {
        if (text == null) {
            return "";
        }
        return text;
    }
}
