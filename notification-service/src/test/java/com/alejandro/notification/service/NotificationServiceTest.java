package com.alejandro.notification.service;

import com.alejandro.notification.event.ReservationCreatedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationServiceTest {

    private final NotificationService notificationService =
            new NotificationService();

    @Test
    void shouldSendReservationCreatedNotification() {

        ReservationCreatedEvent event = new ReservationCreatedEvent(
                1L,
                2L,
                3L,
                "test@email.com",
                "Reunión de prueba",
                LocalDateTime.of(2026, 9, 18, 10, 0),
                LocalDateTime.of(2026, 9, 18, 11, 0)
        );

        assertDoesNotThrow(() ->
                notificationService.sendReservationCreatedNotification(event)
        );
    }
}