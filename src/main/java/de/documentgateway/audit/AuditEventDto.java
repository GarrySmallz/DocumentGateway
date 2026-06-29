package de.documentgateway.audit;

import java.time.Instant;

public record AuditEventDto(
        Long id,
        Instant occurredAt,
        String correlationId,
        String partnerId,
        AuditEventType eventType,
        AuditOutcome outcome,
        String errorCode
) {
    static AuditEventDto from(AuditEvent auditEvent) {
        return new AuditEventDto(
                auditEvent.getId(),
                auditEvent.getOccurredAt(),
                auditEvent.getCorrelationId(),
                auditEvent.getPartnerId(),
                auditEvent.getEventType(),
                auditEvent.getOutcome(),
                auditEvent.getErrorCode()
        );
    }
}
