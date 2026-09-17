package com.alejandro.notification.kafka;

import com.alejandro.notification.event.ReservationCreatedEvent;
import com.alejandro.notification.service.EmailService;
import com.alejandro.notification.service.NotificationService;
import com.resend.core.exception.ResendException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public ReservationEventConsumer(NotificationService notificationService, EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "reservation-created",
            groupId = "notification-service"
    )
    public void consume(ReservationCreatedEvent reservationCreatedEvent) throws ResendException {

        System.out.println("Reservation created: "+ reservationCreatedEvent.reservationId() + " - " + reservationCreatedEvent.title());

        notificationService.sendReservationCreatedNotification(reservationCreatedEvent);
        emailService.sendReservationCreatedEmail(reservationCreatedEvent);
    }
}
