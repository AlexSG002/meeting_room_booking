package com.alejandro.meetingbooking.service.impl;

import com.alejandro.meetingbooking.dto.request.CreateReservationRequest;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.entity.Reservation;
import com.alejandro.meetingbooking.exception.ResourceNotFoundException;
import com.alejandro.meetingbooking.mapper.ReservationMapper;
import com.alejandro.meetingbooking.repository.EmployeeRepository;
import com.alejandro.meetingbooking.repository.ReservationRepository;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepo;
    private final EmployeeRepository employeeRepo;
    private final RoomRepository roomRepo;
    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Reservation reservation = ReservationMapper.toEntity(request);
        reservation.setEmployee(employeeRepo.findById(request.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found")));
        reservation.setRoom(roomRepo.findById(request.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found")));
        Reservation savedReservation = reservationRepo.save(reservation);

        return ReservationMapper.toResponse(savedReservation);
    }
}
