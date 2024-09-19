INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Casa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Educação', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Eletrônicos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Lazer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Restaurante', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Saúde', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Serviços', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Supermercado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Transporte', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Vestuário', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Viagem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

INSERT INTO payment_categories (name, created_at, updated_at)
VALUES ('Trabalho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;
