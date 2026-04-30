package de.documentgateway.message.service;

import de.documentgateway.common.exception.ApiException;
import de.documentgateway.message.client.ReceiverClientMockImpl;
import de.documentgateway.message.routing.RoutingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private XmlSchemaValidationService xmlSchemaValidationService;

    @Mock
    private ReceiverClientMockImpl receiverClient;

    @Mock
    private RoutingService routingService;

    @InjectMocks
    private MessageService messageService;



    @Test
    @DisplayName("processIncomingMessage: blank partnerId -> 400 MISSING_PARTNER_ID")
    void shouldRejectBlankPartnerId() {
        //arrange
            String partnerId = "  ";
            String messageType = "invoice";
            String correlationId = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
            String xml = "<invoice/>";

        //act
        ApiException ex = assertThrows(ApiException.class, () -> {
            messageService.processIncomingMessage(partnerId, messageType, correlationId, xml);
        });

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals("MISSING_PARTNER_ID", ex.getCode());
    }

    @Test
    @DisplayName("processIncomingMessage: unsupported message type -> 400 UNSUPPORTED_MESSAGE_TYPE")
    void shouldRejectUnsupportedMessageType() {
        //arrange
        String partnerId = "partner-a";
        String messageType = "unknown-type";
        String correlationId = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
        String xml = "<invoice/>";

        //act
        ApiException ex = assertThrows(ApiException.class, () -> {
            messageService.processIncomingMessage(partnerId, messageType, correlationId, xml);
        });

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals("UNSUPPORTED_MESSAGE_TYPE", ex.getCode());
    }

    @Test
    @DisplayName("processIncomingMessage: blank payload -> 400 PAYLOAD_EMPTY")
    void shouldRejectBlankPayload() {
        //arrange
        String partnerId = "partner-a";
        String messageType = "invoice";
        String correlationId = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
        String xml = "   ";

        //act
        ApiException ex = assertThrows(ApiException.class, () -> {
            messageService.processIncomingMessage(partnerId, messageType, correlationId, xml);
        });

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals("PAYLOAD_EMPTY", ex.getCode());
    }

    @Test
    @DisplayName("processIncomingMessage: generate correlationId if not existing")
    void shouldGenerateCorrelationIdIfNotProvided() {
        //arrange
        String partnerId = "partner-a";
        String messageType = "invoice";
        String correlationId = null;
        String xml = "<invoice/>";

        //act
        var response = messageService.processIncomingMessage(partnerId, messageType, correlationId, xml);

        //assert
        assertEquals("RECEIVED", response.status());
        assertEquals("Message received successfully", response.message());
        assertEquals(36, response.correlationId().length());
    }

}
