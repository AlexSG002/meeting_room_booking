package com.alejandro.meetingbooking.event;

import java.time.LocalDateTime;

public record ReservationCreatedEvent(
        Long reservationId,
        Long roomId,
        Long employeeId,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
