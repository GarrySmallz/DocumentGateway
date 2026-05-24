package de.documentgateway.message.routing;

import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.client.ReceiverClientMockImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoutingService {

    private final ReceiverClientMockImpl receiverClient;

    final RoutingProperties routingProperties;

    public void routeAndForward(String partnerId, String messageType, String correlationId, String xmlPayload) {

        String routeKey = partnerId.trim() + "." + messageType.trim();
        RoutingProperties.RouteConfig config = routingProperties.getRoutes().get(routeKey);
        if (config == null || config.getReceiverId() == null || config.getReceiverId().isBlank()) {
            throw new
                    ApiException(
                    HttpStatus.NOT_FOUND,
                    "ROUTE_NOT_FOUND",
                    "No route configured for partner/messageType"
            );
        }
        receiverClient.send(config.getReceiverId(), correlationId, xmlPayload);
    }
}
