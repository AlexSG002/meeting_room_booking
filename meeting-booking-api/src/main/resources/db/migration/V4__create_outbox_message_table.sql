CREATE TABLE outbox_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type VARCHAR(100) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    message_key VARCHAR(100) NOT NULL,
    payload LONGTEXT NOT NULL,
    created_at DATETIME NOT NULL,
    published_at DATETIME NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    lease_until DATETIME NULL,
    next_attempt_at DATETIME NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    last_error TEXT NULL
);

CREATE INDEX idx_outbox_pending
    ON outbox_message(status, next_attempt_at, lease_until, created_at, id);
