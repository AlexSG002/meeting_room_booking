package com.alejandro.meetingbooking.outbox;

import com.alejandro.meetingbooking.config.KafkaTopicConfig;
import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxMessageStore outboxMessageStore;

    @Mock
    private ReservationEventSender reservationEventSender;

    private JsonMapper jsonMapper;
    private OutboxPublisher outboxPublisher;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder().build();
        outboxPublisher = new OutboxPublisher(outboxMessageStore, reservationEventSender, jsonMapper);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void marksOutboxMessageAsPublishedAfterKafkaAcknowledgesIt() throws Exception {
        ReservationCreatedEvent event = createEvent();
        OutboxMessage message = createOutboxMessage(event);
        when(outboxMessageStore.claimBatch())
                .thenReturn(List.of(message));
        doNothing().when(reservationEventSender).send(
                eq(KafkaTopicConfig.RESERVATION_CREATED_TOPIC),
                eq("2"),
                any(ReservationCreatedEvent.class));

        outboxPublisher.publishPendingMessages();

        verify(outboxMessageStore).markPublished(message.getId());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void keepsMessagePendingAndSchedulesRetryWhenKafkaSendFails() throws Exception {
        ReservationCreatedEvent event = createEvent();
        OutboxMessage message = createOutboxMessage(event);
        when(outboxMessageStore.claimBatch())
                .thenReturn(List.of(message));
        doThrow(new IllegalStateException("Kafka unavailable"))
                .when(reservationEventSender).send(
                        eq(KafkaTopicConfig.RESERVATION_CREATED_TOPIC),
                        eq("2"),
                        any(ReservationCreatedEvent.class));

        outboxPublisher.publishPendingMessages();

        verify(outboxMessageStore).markFailed(eq(message.getId()), any(IllegalStateException.class));
    }

    @Test
    void failedMessageReturnsToPendingWithExponentialBackoff() {
        OutboxMessage message = OutboxMessage.builder()
                .status(OutboxStatus.PROCESSING)
                .attempts(1)
                .leaseUntil(LocalDateTime.now().plusMinutes(3))
                .build();
        LocalDateTime failureTime = LocalDateTime.of(2026, 11, 9, 12, 0);

        message.markFailed(failureTime, new IllegalStateException("Kafka unavailable"));

        assertEquals(OutboxStatus.PENDING, message.getStatus());
        assertEquals(failureTime.plusSeconds(2), message.getNextAttemptAt());
        assertNull(message.getLeaseUntil());
        assertTrue(message.getLastError().contains("Kafka unavailable"));
    }

    @Test
    void publishedMessageIsNoLongerEligibleForPublishing() {
        OutboxMessage message = OutboxMessage.builder()
                .status(OutboxStatus.PROCESSING)
                .leaseUntil(LocalDateTime.now().plusMinutes(3))
                .build();
        LocalDateTime publishedTime = LocalDateTime.of(2026, 11, 9, 12, 0);

        message.markPublished(publishedTime);

        assertEquals(OutboxStatus.PUBLISHED, message.getStatus());
        assertEquals(publishedTime, message.getPublishedAt());
        assertNull(message.getLeaseUntil());
        assertNull(message.getLastError());
    }

    private ReservationCreatedEvent createEvent() {
        return new ReservationCreatedEvent(
                10L,
                2L,
                3L,
                "employee@example.com",
                "Planning meeting",
                LocalDateTime.of(2026, 12, 10, 10, 0),
                LocalDateTime.of(2026, 12, 10, 11, 0));
    }

    private OutboxMessage createOutboxMessage(ReservationCreatedEvent event) throws Exception {
        return OutboxMessage.builder()
                .id(1L)
                .eventType(KafkaTopicConfig.RESERVATION_CREATED_TOPIC)
                .aggregateId(event.reservationId())
                .messageKey(event.roomId().toString())
                .payload(jsonMapper.writeValueAsString(event))
                .status(OutboxStatus.PROCESSING)
                .attempts(1)
                .leaseUntil(LocalDateTime.now().plusMinutes(3))
                .nextAttemptAt(LocalDateTime.now())
                .build();
    }
}
