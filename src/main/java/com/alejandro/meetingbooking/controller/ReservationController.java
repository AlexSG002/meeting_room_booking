package com.alejandro.meetingbooking.controller;

import com.alejandro.meetingbooking.dto.request.ReservationRequest;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponse getReservationById(@PathVariable Long id) {
        return reservationService.findReservation(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest reservationRequest){
        return reservationService.createReservation(reservationRequest);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id){
        reservationService.cancelReservation(id);
    }
}
