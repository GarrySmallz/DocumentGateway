package de.documentgateway.message.controller;

import de.documentgateway.message.dto.MessageResponse;
import de.documentgateway.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


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
