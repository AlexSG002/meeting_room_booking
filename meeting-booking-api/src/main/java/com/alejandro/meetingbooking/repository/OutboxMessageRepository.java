package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.outbox.OutboxMessage;
import com.alejandro.meetingbooking.outbox.OutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT message FROM OutboxMessage message " +
            "WHERE (message.status = :pending AND message.nextAttemptAt <= :now) " +
            "OR (message.status = :processing AND message.leaseUntil <= :now) " +
            "ORDER BY message.createdAt, message.id")
    List<OutboxMessage> findClaimableForPublishing(
            @Param("pending") OutboxStatus pending,
            @Param("processing") OutboxStatus processing,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}
