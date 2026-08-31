package com.alejandro.meetingbooking.service;

import com.alejandro.meetingbooking.dto.request.ReservationRequest;
import com.alejandro.meetingbooking.dto.response.AvailabilityResponse;
import com.alejandro.meetingbooking.dto.response.AvailableSlotResponse;
import com.alejandro.meetingbooking.dto.response.ReservationResponse;
import com.alejandro.meetingbooking.entity.*;
import com.alejandro.meetingbooking.exception.InvalidReservationException;
import com.alejandro.meetingbooking.exception.ReservationConflictException;
import com.alejandro.meetingbooking.exception.ResourceNotFoundException;
import com.alejandro.meetingbooking.repository.*;
import com.alejandro.meetingbooking.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepo;

    @Mock
    private EmployeeRepository employeeRepo;

    @Mock
    private RoomRepository roomRepo;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Employee createEmployee() {
        return Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .email("john@email.com")
                .build();
    }

    private Room createRoom() {
        return Room.builder()
                .id(1L)
                .name("Sala Prueba")
                .capacity(10)
                .build();
    }

    @Test
    void shouldCreateReservationSuccessfully() {

        Employee employee = createEmployee();
        Room room = createRoom();

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .title("Sprint meeting")
                .startTime(start)
                .endTime(end)
                .employeeId(1L)
                .roomId(1L)
                .build();

        Reservation reservation = Reservation.builder()
                .title("Sprint meeting")
                .startTime(start)
                .endTime(end)
                .employee(employee)
                .room(room)
                .build();

        when(employeeRepo.findById(1L))
                .thenReturn(Optional.of(employee));

        when(roomRepo.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepo.existsOverlappingReservation(
                1L,
                start,
                end
        )).thenReturn(false);

        when(reservationRepo.save(any(Reservation.class)))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.createReservation(request);

        assertNotNull(response);

        verify(employeeRepo).findById(1L);
        verify(roomRepo).findById(1L);

        verify(reservationRepo).existsOverlappingReservation(
                1L,
                start,
                end
        );

        verify(reservationRepo).save(any(Reservation.class));
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsAfterEndTime() {

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.minusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .title("Invalid reservation")
                .startTime(start)
                .endTime(end)
                .employeeId(1L)
                .roomId(1L)
                .build();

        assertThrows(
                InvalidReservationException.class,
                () -> reservationService.createReservation(request)
        );

        verifyNoInteractions(employeeRepo);
        verifyNoInteractions(roomRepo);
        verifyNoInteractions(reservationRepo);
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsInThePast() {

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .title("Past reservation")
                .startTime(start)
                .endTime(end)
                .employeeId(1L)
                .roomId(1L)
                .build();

        assertThrows(
                InvalidReservationException.class,
                () -> reservationService.createReservation(request)
        );

        verifyNoInteractions(employeeRepo);
        verifyNoInteractions(roomRepo);
        verifyNoInteractions(reservationRepo);
    }


    @Test
    void shouldThrowExceptionWhenEmployeeDoesNotExist() {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .title("Meeting")
                .startTime(start)
                .endTime(end)
                .employeeId(99L)
                .roomId(1L)
                .build();

        when(employeeRepo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request)
        );

        verify(employeeRepo).findById(99L);
        verifyNoInteractions(roomRepo);
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRoomIsAlreadyReserved() {

        Employee employee = createEmployee();
        Room room = createRoom();

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .title("Meeting")
                .startTime(start)
                .endTime(end)
                .employeeId(1L)
                .roomId(1L)
                .build();

        when(employeeRepo.findById(1L))
                .thenReturn(Optional.of(employee));

        when(roomRepo.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepo.existsOverlappingReservation(
                1L, start, end
        )).thenReturn(true);

        assertThrows(
                ReservationConflictException.class,
                () -> reservationService.createReservation(request)
        );

        verify(reservationRepo).existsOverlappingReservation(
                1L, start, end
        );

        verify(reservationRepo, never())
                .save(any());
    }

    @Test
    void shouldCancelReservationSuccessfully() {

        Reservation reservation = Reservation.builder()
                .id(1L)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        verify(reservationRepo).findById(1L);
        verify(reservationRepo).delete(reservation);
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonExistingReservation() {

        when(reservationRepo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.cancelReservation(99L)
        );

        verify(reservationRepo, never()).delete(any());
    }

    @Test
    void shouldReturnFullAvailabilityWhenThereAreNoReservations() {

        Room room = createRoom();

        LocalDate date = LocalDate.of(2026, 8, 10);

        when(roomRepo.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepo.findReservationsForPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        AvailabilityResponse response =
                reservationService.getAvailability(1L, date);

        assertEquals(1, response.getAvailableSlots().size());

        AvailableSlotResponse slot =
                response.getAvailableSlots().getFirst();

        assertEquals(
                LocalDateTime.of(date, LocalTime.of(8, 0)),
                slot.getStartTime()
        );

        assertEquals(
                LocalDateTime.of(date, LocalTime.of(20, 0)),
                slot.getEndTime()
        );
    }

    @Test
    void shouldReturnAvailabilityBeforeAndAfterReservation() {

        LocalDate date = LocalDate.of(2026, 8, 10);

        Room room = createRoom();

        Reservation reservation = Reservation.builder()
                .startTime(date.atTime(10, 0))
                .endTime(date.atTime(11, 0))
                .room(room)
                .build();

        when(roomRepo.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepo.findReservationsForPeriod(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(reservation));

        AvailabilityResponse response =
                reservationService.getAvailability(1L, date);

        assertEquals(2, response.getAvailableSlots().size());

        assertEquals(
                date.atTime(8, 0),
                response.getAvailableSlots().get(0).getStartTime()
        );

        assertEquals(
                date.atTime(10, 0),
                response.getAvailableSlots().get(0).getEndTime()
        );

        assertEquals(
                date.atTime(11, 0),
                response.getAvailableSlots().get(1).getStartTime()
        );

        assertEquals(
                date.atTime(20, 0),
                response.getAvailableSlots().get(1).getEndTime()
        );
    }

}
