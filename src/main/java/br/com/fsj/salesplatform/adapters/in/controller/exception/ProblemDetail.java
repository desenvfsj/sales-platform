package br.com.fsj.salesplatform.adapters.in.controller.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        List<ValidationError> errors
) {
    public ProblemDetail(
            String type,
            String title,
            int status,
            String detail,
            String instance,
            Instant timestamp
    ) {
        this(type, title, status, detail, instance, timestamp, null);
    }

    public ProblemDetail(
            ProblemType problemType,
            int status,
            String detail,
            String instance
    ) {
        this(
                problemType.getType(),
                problemType.getTitle(),
                status,
                detail,
                instance,
                Instant.now(),
                null
        );
    }

    public ProblemDetail(
            ProblemType problemType,
            int status,
            String detail,
            String instance,
            List<ValidationError> errors
    ) {
        this(
                problemType.getType(),
                problemType.getTitle(),
                status,
                detail,
                instance,
                Instant.now(),
                errors
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ValidationError(
            String field,
            String message,
            Object rejectedValue
    ) {
        public ValidationError(String field, String message) {
            this(field, message, null);
        }
    }
}
