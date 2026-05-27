package de.documentgateway.message.routing;

import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.client.ReceiverClientMockImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RoutingServiceTest {

    @Mock
    private ReceiverClientMockImpl receiverClient;

    @InjectMocks
    private RoutingService routingService;

    @BeforeEach
    void setUp(){
        RoutingProperties properties = new RoutingProperties();

        RoutingProperties.RouteConfig route = new RoutingProperties.RouteConfig();
        route.setReceiverId("receiver-mock-a");
        route.setTargetUrl("http://localhost:8081/mock-a");

        properties.getRoutes().put("partner-a.invoice", route);

        routingService = new RoutingService(receiverClient, properties);
    }


    @Test
    void routeAndForward_shouldCallSend_whenRouteExists() {
        routingService.routeAndForward("partner-a", "invoice", "corr-1", "<xml/>");
        verify(receiverClient).send(eq("receiver-mock-a"), eq("corr-1"), eq("<xml/>"));
    }

    @Test
    void routeAndForward_shouldThrow_whenRouteMissing() {
        RoutingService emptyRouting = new RoutingService(receiverClient, new RoutingProperties());
        ApiException ex = assertThrows(ApiException.class, () ->
                emptyRouting.routeAndForward("unknown", "invoice", "corr-1", "<xml/>"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals("ROUTE_NOT_FOUND", ex.getCode());
        verify(receiverClient, never()).send(any(), any(), any());
    }
    
}