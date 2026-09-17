package com.alejandro.notification.service;

import com.alejandro.notification.event.ReservationCreatedEvent;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendReservationCreatedEmail(
            ReservationCreatedEvent event) throws ResendException {

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Meeting Booking <onboarding@resend.dev>")
                .to(event.employeeEmail())
                .subject("Reserva creada - " + event.title())
                .html("""
                        <h1>Reserva creada correctamente</h1>

                        <p>Tu reserva ha sido creada correctamente.</p>

                        <h2>%s</h2>

                        <p><strong>Sala:</strong> %s</p>
                        <p><strong>Inicio:</strong> %s</p>
                        <p><strong>Fin:</strong> %s</p>
                        """
                        .formatted(
                                event.title(),
                                event.roomId(),
                                event.startTime(),
                                event.endTime()
                        ))
                .build();

        resend.emails().send(params);
    }
}