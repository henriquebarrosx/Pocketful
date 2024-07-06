CREATE SEQUENCE IF NOT EXISTS accounts_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS accounts
(
    id           BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    created_at   TIMESTAMP    DEFAULT  CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    DEFAULT  CURRENT_TIMESTAMP,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);
