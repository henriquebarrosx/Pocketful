CREATE SEQUENCE IF NOT EXISTS payment_frequencies_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE payment_frequencies
(
    id         BIGINT       NOT NULL,
    times      INTEGER,
    created_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    CONSTRAINT pk_payment_frequencies PRIMARY KEY (id)
);
