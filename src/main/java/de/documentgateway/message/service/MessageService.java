package de.documentgateway.message.service;

import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageService {
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

        return new MessageResponse(
                effectiveCorrelationId,
                "RECEIVED",
                "Message received successfully"
        );
    }
}
