package de.documentgateway.security;
import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerSecurityIntegrationTest {
    private static final String VALID_API_KEY = "test-api-key";
    private static final String VALID_XML =
            "<message xmlns=\"http://documentgateway.de/invoice/v1\"><id>1</id></message>";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("POST ohne X-API-KEY -> 401")
    void shouldReturn401_whenApiKeyMissing() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Partner-Id", "partner-a")
                        .header("X-Message-Type", "invoice")
                        .content(VALID_XML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST mit falschem X-API-KEY -> 401")
    void shouldReturn401_whenApiKeyInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Partner-Id", "partner-a")
                        .header("X-Message-Type", "invoice")
                        .header("X-API-KEY", "wrong-key")
                        .content(VALID_XML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST mit gültigem X-API-KEY -> 202")
    void shouldReturn202_whenApiKeyValid() throws Exception {
        when(messageService.processIncomingMessage(any(), any(), any(), any()))
                .thenReturn(new MessageResponse("corr-1", "accepted", "message accepted"));
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Partner-Id", "partner-a")
                        .header("X-Message-Type", "invoice")
                        .header("X-API-KEY", VALID_API_KEY)
                        .content(VALID_XML))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("POST ohne X-Partner-Id -> 401")
    void shouldReturn401_whenXPartnerIdMissing() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Message-Type", "invoice")
                        .header("X-API-KEY", VALID_API_KEY)
                        .content(VALID_XML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST ohne passenden Key für Partner Id -> 401")
    void shouldReturn401_whenUnmatchingKeyAndPartnerId() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Partner-Id", "partner-b")
                        .header("X-Message-Type", "invoice")
                        .header("X-API-KEY", VALID_API_KEY)
                        .content(VALID_XML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Unerwarteter Fehler im Service -> 500")
    void shouldReturn500_whenUnexpectedExceptionThrown() throws Exception {
        when(messageService.processIncomingMessage(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("unexpected failure"));
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_XML)
                        .header("X-Partner-Id", "partner-a")
                        .header("X-Message-Type", "invoice")
                        .header("X-API-KEY", "test-api-key")
                        .content(VALID_XML))
                .andExpect(status().isInternalServerError());
    }
}