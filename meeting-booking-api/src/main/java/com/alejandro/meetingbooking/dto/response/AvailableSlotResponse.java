package com.alejandro.meetingbooking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
