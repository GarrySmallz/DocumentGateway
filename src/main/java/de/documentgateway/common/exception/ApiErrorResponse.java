package de.documentgateway.common.exception;

public record ApiErrorResponse(
        String code,
        String message
) {
}
