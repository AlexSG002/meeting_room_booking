package com.alejandro.meetingbooking.service.impl;

import com.alejandro.meetingbooking.dto.request.CreateReservationRequest;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.entity.Employee;
import com.alejandro.meetingbooking.entity.Reservation;
import com.alejandro.meetingbooking.entity.Room;
import com.alejandro.meetingbooking.exception.InvalidReservationException;
import com.alejandro.meetingbooking.exception.ReservationConflictException;
import com.alejandro.meetingbooking.exception.ResourceNotFoundException;
import com.alejandro.meetingbooking.mapper.ReservationMapper;
import com.alejandro.meetingbooking.repository.EmployeeRepository;
import com.alejandro.meetingbooking.repository.ReservationRepository;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepo;
    private final EmployeeRepository employeeRepo;
    private final RoomRepository roomRepo;
    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {

        if(!request.getStartTime().isBefore(request.getEndTime())){
            throw new InvalidReservationException("The start time must be before the end time.");
        }

        if(!request.getStartTime().isAfter(LocalDateTime.now())){
            throw new InvalidReservationException("Choose a valid start date");
        }

        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Room room = roomRepo.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Reservation reservation = ReservationMapper.toEntity(request);
        reservation.setEmployee(employee);
        reservation.setRoom(room);

        boolean overlapping = reservationRepo.existsOverlappingReservation(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if(overlapping) {
            throw new ReservationConflictException("The room is already reserved for the selected time slot");
        }

        Reservation savedReservation = reservationRepo.save(reservation);

        return ReservationMapper.toResponse(savedReservation);
    }

}
