package com.alejandro.meetingbooking;

import com.alejandro.meetingbooking.dto.request.ReservationRequest;
import com.alejandro.meetingbooking.entity.Employee;
import com.alejandro.meetingbooking.entity.Room;
import com.alejandro.meetingbooking.outbox.OutboxStatus;
import com.alejandro.meetingbooking.repository.EmployeeRepository;
import com.alejandro.meetingbooking.repository.OutboxMessageRepository;
import com.alejandro.meetingbooking.repository.ReservationRepository;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.exception.ReservationConflictException;
import com.alejandro.meetingbooking.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "app.outbox.enabled=false")
class ConcurrentReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Test
    @Timeout(30)
    void onlyOneConcurrentRequestCanReserveTheSameRoomAndTime() throws Exception {
        reservationRepository.deleteAll();
        Employee employee = employeeRepository.save(Employee.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada-" + System.nanoTime() + "@example.com")
                .build());
        Room room = roomRepository.save(Room.builder()
                .name("Concurrency test room")
                .capacity(4)
                .location("Madrid")
                .build());

        LocalDateTime start = LocalDateTime.now().plusDays(2).withNano(0);
        ReservationRequest request = ReservationRequest.builder()
                .title("Concurrent booking")
                .startTime(start)
                .endTime(start.plusHours(1))
                .employeeId(employee.getId())
                .roomId(room.getId())
                .build();

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch startTogether = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> results = List.of(
                    executor.submit(() -> attemptReservation(request, ready, startTogether)),
                    executor.submit(() -> attemptReservation(request, ready, startTogether)));

            assertTrue(ready.await(5, TimeUnit.SECONDS), "Both requests should be ready to start");
            startTogether.countDown();

            long successfulReservations = results.stream()
                    .filter(result -> getResult(result))
                    .count();

            assertEquals(1, successfulReservations);
            assertEquals(1, reservationRepository.count());
            assertEquals(1, outboxMessageRepository.count());
            assertEquals(1, outboxMessageRepository.findAll().stream()
                    .filter(message -> message.getStatus() == OutboxStatus.PENDING)
                    .count());
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean attemptReservation(
            ReservationRequest request,
            CountDownLatch ready,
            CountDownLatch startTogether) throws InterruptedException {
        ready.countDown();
        startTogether.await();
        try {
            reservationService.createReservation(request);
            return true;
        } catch (ReservationConflictException exception) {
            return false;
        }
    }

    private boolean getResult(Future<Boolean> result) {
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            throw new IllegalStateException("Concurrent reservation attempt failed", exception);
        }
    }
}
