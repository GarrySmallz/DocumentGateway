package de.documentgateway.audit;


public record AuditEventDto(
        Long id,
        String occurredAt,
        String correlationId,
        String partnerId,
        String eventType,
        String outcome,
        String errorCode
) {
    static AuditEventDto from(AuditEvent auditEvent) {
        return new AuditEventDto(
                auditEvent.getId(),
                auditEvent.getOccurredAt() != null ? auditEvent.getOccurredAt().toString() : null,
                auditEvent.getCorrelationId(),
                auditEvent.getPartnerId(),
                auditEvent.getEventType() != null ? auditEvent.getEventType().name() : null,
                auditEvent.getOutcome() != null ? auditEvent.getOutcome().name() : null,
                auditEvent.getErrorCode()
        );
    }
}
