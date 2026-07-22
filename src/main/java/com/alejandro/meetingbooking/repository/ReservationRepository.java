package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(r) > 0 " +
            "FROM Reservation r " +
            "WHERE r.room.id =:roomId " +
            "AND r.startTime <:endTime " +
            "AND r.endTime >:startTime")
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
            );
}
