package com.alejandro.meetingbooking.kafka.consumer;

import com.alejandro.meetingbooking.config.KafkaTopicConfig;
import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationEventConsumer {

    @KafkaListener(
            topics = KafkaTopicConfig.RESERVATION_CREATED_TOPIC,
            groupId = "meeting-booking-group"
    )
    public void consumeReservationCreatedEvent(ReservationCreatedEvent reservationCreatedEvent) {
        log.info("Consume reservation created: {}", reservationCreatedEvent);
    }
}
