package de.documentgateway.message.controller;


import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Standalone {@link MockMvc} ohne Spring-Security- und Servlet-Filter — testet nur den Controller.
 * API-Key-Auth wird in Integrations- bzw. separaten Security-Tests abgedeckt.
 */
@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MessageController controller = new MessageController(messageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/v1/messages -> 202 success")
    void shouldReturnAcceptedWhenRequestIsValid() throws Exception {

        MessageResponse response = new MessageResponse(
                "test-correlation-id",
                "RECEIVED",
                "Message received successfully"
        );

        when(messageService.processIncomingMessage(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyString()
        )).thenReturn(response);

        String xml = "<message><id>1</id></message>";

        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_XML)
                .header("X-Partner-Id", "partner-a")
                .header("X-Message-Type", "invoice")
                .header("X-Correlation-Id", "test-correlation-id")
                .content(xml))
                .andExpect(status().isAccepted());
    }
}
