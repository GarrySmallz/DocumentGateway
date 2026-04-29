package de.documentgateway.common.exception;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        String correlationId,
        String code,
        String message 
) {
}
