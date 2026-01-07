package br.com.fsj.salesplatform.adapters.in.controller.exception;

import br.com.fsj.salesplatform.application.core.exception.BusinessException;
import br.com.fsj.salesplatform.application.core.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_JSON = "application/problem+json";

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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {
        // Lógica simplificada pois HandlerMethodValidationException tem API complexa e Visitor
        // Assume validação falhou
        
        ProblemDetail problem = new ProblemDetail(
                ProblemType.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetros de requisição inválidos: " + ex.getMessage(),
                request.getRequestURI());

        log.warn("Parameter validation error on {}", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

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

    private ProblemDetail.ValidationError mapFieldError(FieldError fieldError) {
        return new ProblemDetail.ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue());
    }
}
