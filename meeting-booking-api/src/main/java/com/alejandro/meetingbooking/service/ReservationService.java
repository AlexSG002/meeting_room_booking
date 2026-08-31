package com.alejandro.meetingbooking.service;

import com.alejandro.meetingbooking.dto.request.ReservationRequest;
import com.alejandro.meetingbooking.dto.response.AvailabilityResponse;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    void cancelReservation(Long reservationId);
    ReservationResponse findReservation(Long reservationId);
    List<ReservationResponse> findAll();
    AvailabilityResponse getAvailability(Long roomId, LocalDate date);
}
