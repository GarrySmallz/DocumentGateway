package de.documentgateway.message;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class MessageProcessingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:messages/valid-invoice.xml")
    private Resource validInvoiceXml;

    @Test
    @DisplayName("POST mit gültigem XML -> 202")
    void shouldReturn202_whenValidInvoiceMessageIsPosted() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_XML)
                .header("X-Partner-Id", "partner-a")
                .header("X-Message-Type", "invoice")
                .header("X-API-KEY", "test-api-key")
                .content(validInvoiceXml.getContentAsString(StandardCharsets.UTF_8)))
                .andExpect(status().isAccepted());
    }

}
