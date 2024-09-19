CREATE TABLE IF NOT EXISTS accounts
(
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    DEFAULT  CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    DEFAULT  CURRENT_TIMESTAMP
);
