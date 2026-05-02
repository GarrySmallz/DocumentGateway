package de.documentgateway.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository repository;


    private void persist(String correlationId, String partnerId, AuditEventType eventType, AuditOutcome outcome, String errorCode) {
        AuditEvent e = new AuditEvent();
        e.setCorrelationId(correlationId);
        e.setPartnerId(partnerId);
        e.setEventType(eventType);
        e.setOutcome(outcome);
        e.setErrorCode(errorCode);
        e.setOccurredAt(Instant.now());
        repository.save(e);
    }


    public void logMessageReceived(String correlationId, String partnerId, AuditEventType eventType, AuditOutcome outcome) {
        persist(correlationId, partnerId, eventType, outcome, null);
    }


    public void logSuccess(String correlationId, String partnerId, AuditEventType eventType, AuditOutcome outcome ) {
        persist(correlationId, partnerId, eventType, outcome, null);
    }

    public void logFailure(String correlationId, String partnerId, AuditEventType eventType, AuditOutcome outcome, String message ) {
        persist(correlationId, partnerId, eventType, outcome, message);
    }
}
