CREATE TABLE IF NOT EXISTS WALLETS
(
    id        BIGSERIAL PRIMARY KEY,
    wallet_id VARCHAR(100),
    user_id   VARCHAR(100),
    balance   BIGINT
);

CREATE INDEX idx_wallet_user_id ON WALLETS (user_id);
CREATE INDEX idx_wallet_wallet_id ON WALLETS (wallet_id);