package com.alejandro.notification.kafka;

import com.alejandro.notification.event.ReservationCreatedEvent;
import com.alejandro.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationEventConsumer {

    private final NotificationService notificationService;

    public ReservationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "reservation-created",
            groupId = "notification-service"
    )
    public void consume(ReservationCreatedEvent reservationCreatedEvent) {

        System.out.println("Reservation created: "+ reservationCreatedEvent.reservationId() + " - " + reservationCreatedEvent.title());

        notificationService.sendReservationCreatedNotification(reservationCreatedEvent);
    }
}
