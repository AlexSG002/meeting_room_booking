package com.alejandro.meetingbooking.service;

import com.alejandro.meetingbooking.dto.request.CreateReservationRequest;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse createReservation(CreateReservationRequest request);
}
