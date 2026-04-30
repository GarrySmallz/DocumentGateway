package de.documentgateway.message.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.documentgateway.common.exception.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReceiverClientMockImplTest {


    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(ReceiverClientMockImpl.class);

        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        logger.addAppender(this.listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(ReceiverClientMockImpl.class);
        logger.detachAppender(this.listAppender);
    }

    @Test
    @DisplayName("send: should log the message being sent")
    void shouldLogMessageWhenSend() {
        //arrange
        String correlationId = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
        String xml = "<invoice/>";

        ReceiverClientMockImpl client = new ReceiverClientMockImpl();

        //act
        client.send("receiver-mock-a", correlationId, xml);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(msg -> assertThat(msg).contains("event=receiver_send_attempt"))
                .anySatisfy(msg -> assertThat(msg).contains("event=receiver_send_success"));
    }

    @Test
    @DisplayName("send: should fail if no receiverId provided")
    void shouldFailIfNoReceiverId() {
        //arrange
        String correlationId = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
        String xml = "<invoice/>";

        ReceiverClientMockImpl client = new ReceiverClientMockImpl();

        //act
        ApiException ex = assertThrows(ApiException.class, () -> {
            client.send("  ", correlationId, xml);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals("RECEIVER_ID_BLANK", ex.getCode());
        assertThat(listAppender.list)
                .anySatisfy(event -> {
                    assertThat(event.getLevel()).isEqualTo(Level.ERROR);
                    assertThat(event.getFormattedMessage()).contains("event=receiver_send_failure");
                    assertThat(event.getFormattedMessage()).contains("reason=receiverId_blank");
                });

    }
}
