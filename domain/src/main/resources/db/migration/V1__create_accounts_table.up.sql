CREATE TABLE IF NOT EXISTS ACCOUNTS
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      VARCHAR(100),
    name         VARCHAR(100),
    email        VARCHAR(100),
    phone_number VARCHAR(100),
    user_type    VARCHAR(100)
);

CREATE INDEX idx_accounts_user_id ON ACCOUNTS(user_id);