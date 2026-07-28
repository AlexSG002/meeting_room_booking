package com.alejandro.meetingbooking.mapper;

import com.alejandro.meetingbooking.dto.request.CreateReservationRequest;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.entity.Reservation;

public final class ReservationMapper {

    private ReservationMapper(){

    }

    public static Reservation toEntity(CreateReservationRequest request){
        return Reservation.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    public static ReservationResponse toResponse(Reservation reservation){
        return ReservationResponse.builder()
                .id(reservation.getId())
                .title(reservation.getTitle())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .roomName(reservation.getRoom().getName())
                .employeeName(reservation.getEmployee().getFirstName() + " " + reservation.getEmployee().getLastName())
                .build();
    }
}
