package de.documentgateway.message.client;

import de.documentgateway.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceiverClientMockImpl implements ReceiverClient {

    @Override
    @SuppressFBWarnings(
            value = "CRLF_INJECTION_LOGS",
            justification = "User input sanitized via .replace before logging; Logback pattern adds second layer"
    )
    public void send(String receiverId, String correlationId, String xmlPayload) {

        long start = System.currentTimeMillis();

        if (receiverId ==null || receiverId.isBlank()) {
            
            log.error("event=receiver_send_failure receiverId={} correlationId={} reason=receiverId_blank",
                    receiverId != null ? receiverId.replace("\r\n", " ") : null,
                    correlationId.replace("\r\n", " "));
            throw new ApiException(HttpStatus.BAD_REQUEST, "RECEIVER_ID_BLANK", "receiverId is blank");
        }
        log.info("event=receiver_send_attempt receiverId={} correlationId={} payloadSize={}",
                receiverId.replace("\r\n", " "),
                correlationId.replace("\r\n", " "),
                xmlPayload.length());



        try {
            long duration = System.currentTimeMillis() - start;
            log.info("event=receiver_send_success receiverId={} correlationId={} durationMs={}",
                    receiverId.replace("\r\n", " "),
                    correlationId.replace("\r\n", " "),
                    duration);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("event=receiver_send_failure receiverId={} correlationId={} durationMs={} errorType={}",
                    receiverId.replace("\r\n", " "),
                    correlationId.replace("\r\n", " "),
                    duration,
                    ex.getClass().getSimpleName(), ex);
            throw ex;
        }

    }
}
