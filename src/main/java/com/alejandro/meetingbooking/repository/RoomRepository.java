package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
