CREATE TABLE IF NOT EXISTS TRANSACTIONS
(
    id               BIGSERIAL PRIMARY KEY,
    transaction_id   VARCHAR(100),
    reference_id     VARCHAR(100),
    credited_wallet  VARCHAR(100),
    debited_wallet   VARCHAR(100),
    description      VARCHAR(255),
    amount           BIGINT,
    transaction_type VARCHAR(100),
    status           VARCHAR(100),
    created_at       timestamp,
    updated_at       timestamp
);
CREATE INDEX idx_transaction_trx_id ON TRANSACTIONS (transaction_id);
CREATE INDEX idx_transaction_ref_id ON TRANSACTIONS (reference_id);