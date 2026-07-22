package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
