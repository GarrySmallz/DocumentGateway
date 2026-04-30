package de.documentgateway.message.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceiverClientMockImpl implements ReceiverClient {

    @Override
    public void send(String receiverId, String correlationId, String xmlPayload) {

        long start = System.currentTimeMillis();
        log.info("event=receiver_send_attempt receiverId={} correlationId={} payloadSize={}",
                receiverId, correlationId, xmlPayload.length());

        try {
            long duration = System.currentTimeMillis() - start;
            log.info("event=receiver_send_success receiverId={} correlationId={} durationMs={}",
                    receiverId, correlationId, duration);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("event=receiver_send_failure receiverId={} correlationId={} durationMs={} errorType={}",
                    receiverId, correlationId, duration, ex.getClass().getSimpleName(), ex);
            throw ex;
        }

    }
}
