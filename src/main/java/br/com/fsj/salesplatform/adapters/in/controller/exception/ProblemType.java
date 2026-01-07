package br.com.fsj.salesplatform.adapters.in.controller.exception;

public enum ProblemType {
    
    VALIDATION_ERROR(
            "https://api.fsj.com.br/problems/validation-error",
            "Validation Error"
    ),
    
    RESOURCE_NOT_FOUND(
            "https://api.fsj.com.br/problems/resource-not-found",
            "Resource Not Found"
    ),
    
    BUSINESS_RULE_VIOLATION(
            "https://api.fsj.com.br/problems/business-rule-violation",
            "Business Rule Violation"
    ),
    
    INVALID_STATE(
            "https://api.fsj.com.br/problems/invalid-state",
            "Invalid State"
    ),
    
    INTERNAL_ERROR(
            "https://api.fsj.com.br/problems/internal-error",
            "Internal Server Error"
    );
    
    private final String type;
    private final String title;
    
    ProblemType(String type, String title) {
        this.type = type;
        this.title = title;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
}
