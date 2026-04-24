package de.documentgateway.common.exception;

import jakarta.validation.ValidationException;
import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                "BAD_REQUEST",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(ValidationException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                "VALIDATION_ERROR",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleIntegrationException(IntegrationException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                "INTEGRATION_ERROR",
                ex.getMessage()
        );
        return ResponseEntity.internalServerError().body(body);
    }
}

