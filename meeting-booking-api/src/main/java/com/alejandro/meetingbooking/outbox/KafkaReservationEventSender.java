package com.alejandro.meetingbooking.outbox;

import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class KafkaReservationEventSender implements ReservationEventSender {

    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

    @Override
    public void send(String topic, String messageKey, ReservationCreatedEvent event)
            throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, messageKey, event)
                .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
