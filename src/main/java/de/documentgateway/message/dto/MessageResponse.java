package de.documentgateway.message.dto;

public record MessageResponse(
        String correlationId,
        String status,
        String message
) {
}
