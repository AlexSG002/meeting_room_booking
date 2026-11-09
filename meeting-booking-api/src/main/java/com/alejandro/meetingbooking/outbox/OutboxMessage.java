package com.alejandro.meetingbooking.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false, length = 100)
    private String messageKey;

    @Column(nullable = false, columnDefinition = "longtext")
    private String payload;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    private LocalDateTime leaseUntil;

    @Column(nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(nullable = false)
    @Builder.Default
    private int attempts = 0;

    @Column(columnDefinition = "text")
    private String lastError;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        if (nextAttemptAt == null) {
            nextAttemptAt = now;
        }
    }

    public void markClaimed(LocalDateTime leaseExpiration) {
        status = OutboxStatus.PROCESSING;
        leaseUntil = leaseExpiration;
        attempts++;
    }

    public void markPublished(LocalDateTime publishedTime) {
        status = OutboxStatus.PUBLISHED;
        publishedAt = publishedTime;
        leaseUntil = null;
        lastError = null;
    }

    public void markFailed(LocalDateTime failureTime, Throwable error) {
        long delaySeconds = Math.min(3600, 1L << Math.min(attempts, 12));
        status = OutboxStatus.PENDING;
        leaseUntil = null;
        nextAttemptAt = failureTime.plusSeconds(delaySeconds);

        String message = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
        lastError = message.length() > 2000 ? message.substring(0, 2000) : message;
    }
}
