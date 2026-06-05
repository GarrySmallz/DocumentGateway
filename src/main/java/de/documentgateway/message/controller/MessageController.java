package de.documentgateway.message.controller;

import de.documentgateway.common.exception.ApiErrorResponse;
import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.service.MessageService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Nachricht erfolgreich verarbeitet",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Fehlender Header oder ungültiger Body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Ungültiger API-Key oder Partner-Id",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "XSD-Validierungsfehler",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Interner Fehler",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })

    @PostMapping
    public ResponseEntity<MessageResponse> receiveMessage(
          @RequestHeader("X-Partner-Id") String partnerId,
          @RequestHeader("X-Message-Type") String messageType,
          @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
          @RequestBody String xmlPayload
    ) {
        MessageResponse response = messageService.processIncomingMessage(
                partnerId,
                messageType,
                correlationId,
                xmlPayload
        );
        return ResponseEntity.accepted().body(response);
    }
}
