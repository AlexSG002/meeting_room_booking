package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {

    @Modifying
    @Query(value = "INSERT IGNORE INTO processed_event " +
            "(event_key, event_type, aggregate_id, processed_at) " +
            "VALUES (:eventKey, :eventType, :aggregateId, :processedAt)", nativeQuery = true)
    int insertIfAbsent(
            @Param("eventKey") String eventKey,
            @Param("eventType") String eventType,
            @Param("aggregateId") Long aggregateId,
            @Param("processedAt") LocalDateTime processedAt);
}
