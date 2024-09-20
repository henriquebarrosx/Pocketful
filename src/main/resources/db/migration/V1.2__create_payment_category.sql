CREATE SEQUENCE payment_categories_seq START 1;

CREATE TABLE IF NOT EXISTS payment_categories
(
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL    UNIQUE,
    created_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP
);
