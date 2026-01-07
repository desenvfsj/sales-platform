package br.com.fsj.salesplatform.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Record que representa um Problem Detail seguindo RFC 9457.
 *
 * <p>Este é o formato padrão de resposta de erro da API, garantindo
 * consistência e conformidade com o padrão internacional RFC 9457.</p>
 *
 * <p><strong>Campos obrigatórios:</strong></p>
 * <ul>
 *   <li>type: URI que identifica o tipo do problema</li>
 *   <li>title: Resumo curto e legível do problema</li>
 *   <li>status: Código de status HTTP</li>
 *   <li>detail: Explicação específica desta ocorrência</li>
 * </ul>
 *
 * <p><strong>Campos opcionais:</strong></p>
 * <ul>
 *   <li>instance: URI que identifica a ocorrência específica</li>
 *   <li>timestamp: Quando o erro ocorreu</li>
 *   <li>errors: Lista de erros de validação (para status 400/422)</li>
 * </ul>
 *
 * <p><strong>Exemplo de uso:</strong></p>
 * <pre>{@code
 * {
 *   "type": "https://api.fsj.com.br/problems/validation-error",
 *   "title": "Validation Error",
 *   "status": 400,
 *   "detail": "Um ou mais campos possuem valores inválidos",
 *   "instance": "/api/v1/carts/123",
 *   "timestamp": "2025-12-26T10:30:00Z",
 *   "errors": [
 *     {"field": "email", "message": "Email inválido"}
 *   ]
 * }
 * }</pre>
 *
 * @param type      URI que identifica o tipo do problema
 * @param title     resumo curto e legível do problema
 * @param status    código de status HTTP
 * @param detail    explicação específica desta ocorrência
 * @param instance  URI que identifica a ocorrência específica (opcional)
 * @param timestamp quando o erro ocorreu
 * @param errors    lista de erros de validação (opcional)
 * @author Sales Platform Team
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9457.html">RFC 9457</a>
 * @since 1.0.0
 */
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
    /**
     * Construtor simplificado sem erros de validação.
     */
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

    /**
     * Construtor a partir de ProblemType.
     */
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

    /**
     * Construtor a partir de ProblemType com erros de validação.
     */
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
        /**
         * Construtor simplificado sem valor rejeitado.
         */
        public ValidationError(String field, String message) {
            this(field, message, null);
        }
    }
}

