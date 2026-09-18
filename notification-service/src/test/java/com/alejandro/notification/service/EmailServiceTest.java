package com.alejandro.notification.service;

import com.alejandro.notification.event.ReservationCreatedEvent;
import com.resend.Resend;
import com.resend.services.emails.Emails;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void shouldSendReservationCreatedEmail() throws Exception {

        Resend resend = mock(Resend.class);
        Emails emails = mock(Emails.class);

        when(resend.emails()).thenReturn(emails);

        EmailService emailService = new EmailService(resend);

        ReservationCreatedEvent event = new ReservationCreatedEvent(
                1L,
                2L,
                3L,
                "test@email.com",
                "Reunión de prueba",
                LocalDateTime.of(2026, 9, 18, 10, 0),
                LocalDateTime.of(2026, 9, 18, 11, 0)
        );

        emailService.sendReservationCreatedEmail(event);

        verify(resend).emails();
        verify(emails).send(any());
    }
}