package de.documentgateway.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "audit_event")
public class AuditEvent {


    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 36)
    private String correlationId;

    private String partnerId;

    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    @Enumerated(EnumType.STRING)
    private AuditOutcome outcome;

    private String errorCode;


}