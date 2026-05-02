package de.documentgateway.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    private static final String CORRELATION_ID = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
    private static final String PARTNER_ID = "partner-a";

    @Mock
    private AuditEventRepository repository;

    @InjectMocks
    private AuditService auditService;

    @Test
    @DisplayName("logMessageReceived: should save correct audit event")
    void logMessageReceived() {
        // act
        auditService.logMessageReceived(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.MESSAGE_RECEIVED
        );

        // assert
        assertSaved(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.MESSAGE_RECEIVED,
                null,
                null
        );
    }

    @Test
    @DisplayName("logSuccess: should save correct audit event")
    void logSuccess() {
        // act
        auditService.logSuccess(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.XSD_VALIDATION,
                AuditOutcome.SUCCESS
        );

        // assert
        assertSaved(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.XSD_VALIDATION,
                AuditOutcome.SUCCESS,
                null
        );
    }

    @Test
    @DisplayName("logFailure: should save correct audit event")
    void logFailure() {
        // arrange
        String errorCode = "XML_VALIDATION_FAILED";

        // act
        auditService.logFailure(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.XSD_VALIDATION,
                AuditOutcome.FAILURE,
                errorCode
        );

        // assert
        assertSaved(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.XSD_VALIDATION,
                AuditOutcome.FAILURE,
                errorCode
        );
    }

    private void assertSaved(
            String correlationId,
            String partnerId,
            AuditEventType eventType,
            AuditOutcome expectedOutcome,
            String expectedErrorCode
    ) {
        verify(repository).save(argThat(event ->
                event.getCorrelationId().equals(correlationId)
                        && event.getPartnerId().equals(partnerId)
                        && event.getEventType() == eventType
                        && Objects.equals(event.getOutcome(), expectedOutcome)
                        && Objects.equals(event.getErrorCode(), expectedErrorCode)
                        && event.getOccurredAt() != null
        ));
    }
}
