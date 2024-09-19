CREATE TABLE IF NOT EXISTS payment_frequencies
(
    id         BIGSERIAL    PRIMARY KEY,
    times      INTEGER,
    created_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP
);
