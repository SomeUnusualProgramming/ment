package com.document.analyzer.domain;

public enum RiskCategory {
    LEGAL_COMPLIANCE("Legal & Compliance"),
    FINANCIAL("Financial"),
    OPERATIONAL("Operational"),
    CONTRACTUAL("Contractual"),
    DATA_PRIVACY("Data Privacy"),
    SECURITY("Security"),
    OTHER("Other");

    private final String displayName;

    RiskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
