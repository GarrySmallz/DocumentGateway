package de.documentgateway.message.service;

import de.documentgateway.audit.AuditEventType;
import de.documentgateway.audit.AuditOutcome;
import de.documentgateway.audit.AuditService;
import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.routing.RoutingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageService {

    private final XmlSchemaValidationService xmlSchemaValidationService;

    private final RoutingService routingService;

    private final AuditService auditService;

    public MessageResponse processIncomingMessage(
            String partnerId,
            String messageType,
            String correlationId,
            String xmlPayload
    ) {
        if (partnerId == null || partnerId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "MISSING_PARTNER_ID", "X-Partner-Id header is required");
        }
        if (!"invoice".equals(messageType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_MESSAGE_TYPE", "message type is not supported");
        }
        if (xmlPayload == null || xmlPayload.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PAYLOAD_EMPTY", "message payload is null or empty");
        }

        String effectiveCorrelationId =
                (correlationId == null || correlationId.isBlank())
                        ? UUID.randomUUID().toString()
                        : correlationId.trim();


        auditService.logMessageReceived(effectiveCorrelationId, partnerId, AuditEventType.MESSAGE_RECEIVED);

        try {
            xmlSchemaValidationService.validate(xmlPayload);
            auditService.logSuccess(
                    effectiveCorrelationId,
                    partnerId,
                    AuditEventType.XSD_VALIDATION,
                    AuditOutcome.SUCCESS);

        } catch (ApiException e) {
            auditService.logFailure(
                    effectiveCorrelationId,
                    partnerId,
                    AuditEventType.XSD_VALIDATION,
                    AuditOutcome.FAILURE,
                    e.getCode());
            throw e;
        }



        try {
            routingService.routeAndForward(partnerId, messageType, effectiveCorrelationId, xmlPayload);
            auditService.logSuccess(
                    effectiveCorrelationId,
                    partnerId,
                    AuditEventType.ROUTING,
                    AuditOutcome.SUCCESS
                    );
        } catch (ApiException e) {
            auditService.logFailure(effectiveCorrelationId,
                    partnerId,
                    AuditEventType.ROUTING,
                    AuditOutcome.FAILURE,
                    e.getCode());
            throw e;
        }


        return new MessageResponse(
                effectiveCorrelationId,
                "RECEIVED",
                "Message received successfully"
        );
    }
}
