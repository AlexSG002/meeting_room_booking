package com.alejandro.meetingbooking.service.impl;

import com.alejandro.meetingbooking.dto.request.ReservationRequest;
import com.alejandro.meetingbooking.dto.response.AvailabilityResponse;
import com.alejandro.meetingbooking.dto.response.AvailableSlotResponse;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.entity.Employee;
import com.alejandro.meetingbooking.entity.Reservation;
import com.alejandro.meetingbooking.entity.Room;
import com.alejandro.meetingbooking.event.ReservationCreatedEvent;
import com.alejandro.meetingbooking.exception.InvalidReservationException;
import com.alejandro.meetingbooking.exception.ReservationConflictException;
import com.alejandro.meetingbooking.exception.ResourceNotFoundException;
import com.alejandro.meetingbooking.mapper.ReservationMapper;
import com.alejandro.meetingbooking.repository.EmployeeRepository;
import com.alejandro.meetingbooking.repository.ReservationRepository;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.service.ReservationService;
import com.alejandro.meetingbooking.kafka.producer.ReservationEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepo;
    private final EmployeeRepository employeeRepo;
    private final RoomRepository roomRepo;
    private final ReservationEventProducer reservationEventProducer;
    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

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

        ReservationCreatedEvent event = new ReservationCreatedEvent(savedReservation.getId(),
                savedReservation.getRoom().getId(),
                savedReservation.getEmployee().getId(),
                savedReservation.getEmployee().getEmail(),
                savedReservation.getTitle(),
                savedReservation.getStartTime(),
                savedReservation.getEndTime());

        reservationEventProducer.sendReservationCreatedEvent(event);

        return ReservationMapper.toResponse(savedReservation);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId).orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservationRepo.delete(reservation);

    }

    @Override
    public ReservationResponse findReservation(Long reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId).orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return ReservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> findAll() {
        List<Reservation> reservations = reservationRepo.findAll();
        return reservations.stream().map(ReservationMapper::toResponse).toList();
    }

    @Override
    public AvailabilityResponse getAvailability(Long roomId, LocalDate date) {
        roomRepo.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        LocalDateTime startOfDay = date.atTime(8,0);
        LocalDateTime endOfDay = date.atTime(20,0);
        List<Reservation> reservations = reservationRepo.findReservationsForPeriod(roomId, startOfDay, endOfDay);
        System.out.println("ROOM ID: " + roomId);
        System.out.println("START: " + startOfDay);
        System.out.println("END: " + endOfDay);
        System.out.println("RESERVATIONS: " + reservations.size());

        for (Reservation reservation : reservations) {
            System.out.println(
                    "RESERVATION: " +
                            reservation.getId() + " | " +
                            reservation.getStartTime() + " -> " +
                            reservation.getEndTime() +
                            " | ROOM: " + reservation.getRoom().getId()
            );
        }
        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        LocalDateTime currentDateTime = startOfDay;
        for(Reservation reservation : reservations) {
            if (currentDateTime.isBefore(reservation.getStartTime())) {
                availableSlots.add(AvailableSlotResponse.builder().startTime(currentDateTime).endTime(reservation
                        .getStartTime()).build());
            }

            if(currentDateTime.isBefore(reservation.getEndTime())) {
                currentDateTime = reservation.getEndTime();
            }
        }

        if(currentDateTime.isBefore(endOfDay)) {
            availableSlots.add(AvailableSlotResponse.builder().startTime(currentDateTime).endTime(endOfDay).build());
        }

        return AvailabilityResponse.builder()
                .roomId(roomId)
                .date(date)
                .availableSlots(availableSlots)
                .build();
    }


}
