package de.documentgateway.common.exception;

import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                correlationId,
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }
}

