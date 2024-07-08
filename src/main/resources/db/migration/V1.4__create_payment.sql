CREATE SEQUENCE IF NOT EXISTS payments_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS payments
(
    id                      BIGINT       NOT NULL,
    amount                  FLOAT        NOT NULL,
    description             VARCHAR(255) NOT NULL,
    payed                   BOOLEAN      NOT NULL,
    is_expense              BOOLEAN      NOT NULL,
    deadline_at             date,
    account_id              BIGINT       NOT NULL,
    payment_category_id     BIGINT       NOT NULL,
    payment_frequency_id    BIGINT       NOT NULL,
    created_at              TIMESTAMP    DEFAULT    CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP    DEFAULT    CURRENT_TIMESTAMP,
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT pk_accounts FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT pk_payment_categories FOREIGN KEY (payment_category_id) REFERENCES payment_categories (id),
    CONSTRAINT pk_payment_frequencies FOREIGN KEY (payment_frequency_id) REFERENCES payment_frequencies (id)
);
