package de.documentgateway.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private String resolveCorrelationId(HttpServletRequest request){
        String header = request.getHeader("X-Correlation-Id");
        if (header == null || header.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return header.trim();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                correlationId,
                "BAD_REQUEST",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                correlationId,
                "VALIDATION_ERROR",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleIntegrationException(IntegrationException ex, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                correlationId,
                "INTEGRATION_ERROR",
                ex.getMessage()
        );
        return ResponseEntity.internalServerError().body(body);
    }
}

