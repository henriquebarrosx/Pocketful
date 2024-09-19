CREATE TABLE IF NOT EXISTS payment_categories
(
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL    UNIQUE,
    created_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP
);
