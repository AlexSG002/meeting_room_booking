package com.alejandro.notification.service;

import com.alejandro.notification.event.ReservationCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendReservationCreatedNotification(
            ReservationCreatedEvent reservationCreatedEvent){
        System.out.println("=================================");
        System.out.println("SENDING EMAIL");
        System.out.println("To: " + reservationCreatedEvent.employeeEmail());
        System.out.println("Subject: Reserva creada - " + reservationCreatedEvent.title());
        System.out.println();
        System.out.println("Tu reserva ha sido creada correctamente.");
        System.out.println("Reserva: " + reservationCreatedEvent.title());
        System.out.println("Sala: " + reservationCreatedEvent.roomId());
        System.out.println("Inicio: " + reservationCreatedEvent.startTime());
        System.out.println("Fin: " + reservationCreatedEvent.endTime());
        System.out.println("=================================");
    }
}
