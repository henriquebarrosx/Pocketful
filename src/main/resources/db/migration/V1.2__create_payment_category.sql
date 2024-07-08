CREATE SEQUENCE IF NOT EXISTS payment_categories_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS payment_categories
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL    UNIQUE,
    created_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT     CURRENT_TIMESTAMP,
    CONSTRAINT pk_payment_categories PRIMARY KEY (id)
);
