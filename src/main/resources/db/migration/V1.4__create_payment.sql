CREATE TABLE IF NOT EXISTS payments
(
    id                      BIGSERIAL    PRIMARY KEY,
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

    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    FOREIGN KEY (payment_category_id) REFERENCES payment_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (payment_frequency_id) REFERENCES payment_frequencies(id) ON DELETE SET NULL
);
