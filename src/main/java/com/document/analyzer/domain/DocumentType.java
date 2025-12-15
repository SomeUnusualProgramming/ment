package com.document.analyzer.domain;

public enum DocumentType {
    PLATFORM_TERMS("Platform Terms"),
    B2B_CONTRACT("B2B Contract"),
    NDA("Non-Disclosure Agreement"),
    EMAIL_CHANGE_OF_TERMS("Email Change of Terms"),
    OTHER("Other");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
