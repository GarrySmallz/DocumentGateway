package de.documentgateway.message.service;

import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.client.ReceiverClientMockImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoutingService {

    private final ReceiverClientMockImpl receiverClient;

    public void routeAndForward(String partnerId, String messageType, String correlationId, String xmlPayload) {

        String receiverId = resolveReceiver(partnerId, messageType);

        receiverClient.send(receiverId, correlationId, xmlPayload);

    }

    private String resolveReceiver(String partnerId, String messageType) {
        if ("partner-a".equals(partnerId) && "invoice".equals(messageType)) {
            return "receiver-mock-a";
        }
        throw new
                ApiException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "ROUTE_NOT_FOUND",
                "No route configured for partner/messageType"
        );
    }
}
