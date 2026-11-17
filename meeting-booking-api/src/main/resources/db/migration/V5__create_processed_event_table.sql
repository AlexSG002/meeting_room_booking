CREATE TABLE processed_event (
    event_key VARCHAR(150) NOT NULL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    processed_at DATETIME NOT NULL,
    CONSTRAINT uk_processed_event_type_aggregate UNIQUE (event_type, aggregate_id)
);
