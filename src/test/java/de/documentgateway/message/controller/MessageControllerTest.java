package de.documentgateway.message.controller;


import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("POST /api/v1/messages -> 202 success")
    void shouldReturnAcceptedWhenRequestIsValid() throws Exception {

        //arrange
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

        //Act + Assert
        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_XML)
                .header("X-Partner-Id", "partner-a")
                .header("X-Message-Type", "invoice")
                .header("X-Correlation-Id", "test-correlation-id")
                .content(xml))
                .andExpect(status().isAccepted());
    }




}
