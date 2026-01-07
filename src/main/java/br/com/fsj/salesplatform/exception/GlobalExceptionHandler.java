package br.com.fsj.salesplatform.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler global de exceções seguindo RFC 9457 (Problem Details for HTTP APIs).
 *
 * <p>
 * Esta classe captura todas as exceções lançadas pelos controllers e as
 * converte
 * em respostas padronizadas no formato Problem Details.
 * </p>
 *
 * <p>
 * <strong>Content-Type:</strong> application/problem+json
 * </p>
 *
 * <p>
 * <strong>Exceções tratadas:</strong>
 * </p>
 * <ul>
 * <li>MethodArgumentNotValidException → 400 (erros de validação em body)</li>
 * <li>HandlerMethodValidationException → 400 (erros de validação em
 * path/params)</li>
 * <li>ResourceNotFoundException → 404 (recurso não encontrado)</li>
 * <li>BusinessException → 400 (violação de regra de negócio)</li>
 * <li>IllegalArgumentException → 400 (argumento inválido)</li>
 * <li>IllegalStateException → 409 (conflito de estado)</li>
 * <li>Exception → 500 (erro interno)</li>
 * </ul>
 *
 * @author Sales Platform Team
 * @see ProblemDetail
 * @see ProblemType
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_JSON = "application/problem+json";

    /**
     * Trata erros de validação de campos no body (Bean Validation).
     *
     * @param ex      exceção de validação
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ProblemDetail.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ProblemDetail problem = new ProblemDetail(
                ProblemType.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos possuem valores inválidos",
                request.getRequestURI(),
                errors);

        log.warn("Validation error on {}: {} field(s) invalid",
                request.getRequestURI(), errors.size());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Trata erros de validação em path variables e request parameters.
     *
     * @param ex      exceção de validação de metodo
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 400
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {
        List<ProblemDetail.ValidationError> errors = new ArrayList<>();

        // Extrair detalhes dos erros de validação
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void pathVariable(@NonNull PathVariable ann, @NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }

            @Override
            public void requestParam(RequestParam ann, @NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }

            @Override
            public void requestHeader(@NonNull RequestHeader ann, @NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }

            @Override
            public void cookieValue(@NonNull CookieValue ann, @NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }

            @Override
            public void matrixVariable(@NonNull MatrixVariable ann, @NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }

            @Override
            public void modelAttribute(ModelAttribute ann, @NonNull ParameterErrors pe) {
                addBeanErrors(pe, errors);
            }

            @Override
            public void requestBody(@NonNull RequestBody ann, @NonNull ParameterErrors pe) {
                addBeanErrors(pe, errors);
            }

            @Override
            public void requestPart(@NonNull RequestPart ann, @NonNull ParameterErrors pe) {
                addBeanErrors(pe, errors);
            }

            @Override
            public void other(@NonNull ParameterValidationResult result) {
                addParamErrors(result, errors);
            }
        });

        // Se não conseguiu extrair erros, adicionar erro genérico
        if (errors.isEmpty()) {
            errors.add(new ProblemDetail.ValidationError(
                    "parameter",
                    "Parâmetro inválido",
                    null));
        }

        ProblemDetail problem = new ProblemDetail(
                ProblemType.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais parâmetros possuem valores inválidos",
                request.getRequestURI(),
                errors);

        log.warn("Parameter validation error on {}: {} error(s)",
                request.getRequestURI(), errors.size());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    private static void addParamErrors(ParameterValidationResult result, List<ProblemDetail.ValidationError> out) {
        for (MessageSourceResolvable e : result.getResolvableErrors()) {
            out.add(new ProblemDetail.ValidationError(result.getMethodParameter().getParameterName(), e.getDefaultMessage()));
        }
    }

    private static void addBeanErrors(ParameterErrors pe, List<ProblemDetail.ValidationError> out) {
        for (FieldError fe : pe.getFieldErrors()) {
            out.add(new ProblemDetail.ValidationError(fe.getField(), fe.getDefaultMessage()));
        }
    }

    /**
     * Trata exceções de recurso não encontrado.
     *
     * <p>Captura qualquer recurso não encontrado: carrinho, produto, item, etc.</p>
     *
     * @param ex      exceção de recurso não encontrado
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                ProblemType.RESOURCE_NOT_FOUND,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI());

        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Trata exceções de argumento ilegal.
     *
     * @param ex      exceção de argumento ilegal
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                ProblemType.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        log.warn("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Trata exceções de regras de negócio.
     *
     * <p>Captura violações de regras de negócio, estados inválidos e conflitos.
     * Retorna status 409 (Conflict) para melhor semântica REST.</p>
     *
     * @param ex      exceção de regra de negócio
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 409
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                ProblemType.BUSINESS_RULE_VIOLATION,
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI());

        log.warn("Business rule violation on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Trata exceções de estado ilegal (fallback para IllegalStateException não tratadas).
     *
     * <p>Recomenda-se usar BusinessException para estados inválidos de negócio.</p>
     *
     * @param ex      exceção de estado ilegal
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                ProblemType.INVALID_STATE,
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI());

        log.warn("Invalid state on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Trata exceções genéricas não capturadas pelos handlers específicos.
     *
     * @param ex      exceção genérica
     * @param request requisição HTTP
     * @return ResponseEntity com Problem Detail e status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                ProblemType.INTERNAL_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
                request.getRequestURI());

        log.error("Internal error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    /**
     * Mapeia um FieldError para ValidationError.
     */
    private ProblemDetail.ValidationError mapFieldError(FieldError fieldError) {
        return new ProblemDetail.ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue());
    }
}
