package de.documentgateway.message.client;

public interface ReceiverClient {
    void send(String receiverId, String correlationId, String xmlPayload);
}
