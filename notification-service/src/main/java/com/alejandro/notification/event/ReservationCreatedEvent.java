package com.alejandro.notification.event;

import java.time.LocalDateTime;

public record ReservationCreatedEvent(
        Long reservationId,
        Long roomId,
        Long employeeId,
        String employeeEmail,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}