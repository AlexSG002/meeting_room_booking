package com.alejandro.notification.kafka;

import com.alejandro.notification.event.ReservationCreatedEvent;
import com.alejandro.notification.service.EmailService;
import com.alejandro.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ReservationEventConsumerTest {

    @Test
    void shouldProcessReservationCreatedEvent() throws Exception {

        NotificationService notificationService =
                mock(NotificationService.class);

        EmailService emailService =
                mock(EmailService.class);

        ReservationEventConsumer consumer =
                new ReservationEventConsumer(
                        notificationService,
                        emailService
                );

        ReservationCreatedEvent event = new ReservationCreatedEvent(
                1L,
                2L,
                3L,
                "test@email.com",
                "Reunión de prueba",
                LocalDateTime.of(2026, 9, 18, 10, 0),
                LocalDateTime.of(2026, 9, 18, 11, 0)
        );

        consumer.consume(event);

        verify(notificationService)
                .sendReservationCreatedNotification(event);

        verify(emailService)
                .sendReservationCreatedEmail(event);
    }
}