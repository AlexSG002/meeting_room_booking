package com.alejandro.meetingbooking.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {
    private Long roomId;
    private LocalDate date;
    private List<AvailableSlotResponse> availableSlots;
}
