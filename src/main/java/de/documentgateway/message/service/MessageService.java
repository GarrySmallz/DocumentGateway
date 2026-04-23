package de.documentgateway.message.service;

import de.documentgateway.message.dto.MessageResponse;
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
            throw new IllegalArgumentException("partnerId is required");
        }
        if (!"invoice".equals(messageType)) {
            throw new IllegalArgumentException("unsupported messageType");
        }
        if (xmlPayload == null || xmlPayload.isBlank()) {
            throw new IllegalArgumentException("xmlPayload is required");
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
