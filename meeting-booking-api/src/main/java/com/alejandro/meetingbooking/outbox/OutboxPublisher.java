package com.alejandro.meetingbooking.outbox;

import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.outbox", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxMessageStore outboxMessageStore;
    private final ReservationEventSender reservationEventSender;
    private final JsonMapper jsonMapper;

    @Scheduled(fixedDelayString = "${app.outbox.polling-interval-ms:1000}")
    public void publishPendingMessages() {
        List<OutboxMessage> pendingMessages = outboxMessageStore.claimBatch();

        for (OutboxMessage message : pendingMessages) {
            try {
                ReservationCreatedEvent event = jsonMapper.readValue(
                        message.getPayload(), ReservationCreatedEvent.class);
                reservationEventSender.send(
                        message.getEventType(),
                        message.getMessageKey(),
                        event);
                outboxMessageStore.markPublished(message.getId());
            } catch (InterruptedException exception) {
                outboxMessageStore.markFailed(message.getId(), exception);
                Thread.currentThread().interrupt();
                log.warn("Interrupted while publishing outbox message {}", message.getId(), exception);
                break;
            } catch (Exception exception) {
                outboxMessageStore.markFailed(message.getId(), exception);
                log.warn("Could not publish outbox message {}; it will be retried", message.getId(), exception);
            }
        }
    }
}
