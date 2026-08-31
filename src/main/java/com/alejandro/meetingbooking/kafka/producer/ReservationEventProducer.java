package com.alejandro.meetingbooking.kafka.producer;

import com.alejandro.meetingbooking.config.KafkaTopicConfig;
import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventProducer {
    private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

    public void sendReservationCreatedEvent(ReservationCreatedEvent reservationCreatedEvent) {
        kafkaTemplate.send(KafkaTopicConfig.RESERVATION_CREATED_TOPIC, reservationCreatedEvent.roomId().toString(), reservationCreatedEvent);
    }
}
