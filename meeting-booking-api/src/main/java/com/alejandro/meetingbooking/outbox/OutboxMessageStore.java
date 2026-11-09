package com.alejandro.meetingbooking.outbox;

import com.alejandro.meetingbooking.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxMessageStore {

    private static final int BATCH_SIZE = 10;
    private static final int LEASE_SECONDS = 180;

    private final OutboxMessageRepository outboxMessageRepo;

    @Transactional
    public List<OutboxMessage> claimBatch() {
        LocalDateTime now = LocalDateTime.now();
        List<OutboxMessage> messages = outboxMessageRepo.findClaimableForPublishing(
                OutboxStatus.PENDING,
                OutboxStatus.PROCESSING,
                now,
                PageRequest.of(0, BATCH_SIZE));

        messages.forEach(message -> message.markClaimed(now.plusSeconds(LEASE_SECONDS)));
        return messages;
    }

    @Transactional
    public void markPublished(Long messageId) {
        outboxMessageRepo.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("Outbox message not found: " + messageId))
                .markPublished(LocalDateTime.now());
    }

    @Transactional
    public void markFailed(Long messageId, Throwable error) {
        outboxMessageRepo.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("Outbox message not found: " + messageId))
                .markFailed(LocalDateTime.now(), error);
    }
}
