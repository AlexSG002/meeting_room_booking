package com.alejandro.meetingbooking.kafka.consumer;

import com.alejandro.meetingbooking.config.KafkaTopicConfig;
import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import com.alejandro.meetingbooking.repository.ProcessedEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ReservationEventConsumer {

    private static final String EVENT_TYPE = "reservation-created";

    private final ProcessedEventRepository processedEventRepo;

    public ReservationEventConsumer(ProcessedEventRepository processedEventRepo) {
        this.processedEventRepo = processedEventRepo;
    }

    @KafkaListener(
            topics = KafkaTopicConfig.RESERVATION_CREATED_TOPIC,
            groupId = "meeting-booking-group"
    )
    @Transactional
    public void consumeReservationCreatedEvent(ReservationCreatedEvent reservationCreatedEvent) {
        String eventKey = EVENT_TYPE + ":" + reservationCreatedEvent.reservationId();
        int inserted = processedEventRepo.insertIfAbsent(
                eventKey,
                EVENT_TYPE,
                reservationCreatedEvent.reservationId(),
                java.time.LocalDateTime.now());

        if (inserted == 0) {
            log.info("Skipping duplicate reservation-created event {}", eventKey);
            return;
        }

        log.info("Consume reservation created: {}", reservationCreatedEvent);
    }
}
