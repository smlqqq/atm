CREATE TABLE IF NOT EXISTS atm_transactions
(
    id               BIGSERIAL PRIMARY KEY,
    transaction_type VARCHAR(25) NOT NULL,
    amount           BIGINT      NOT NULL,
    timestamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sender           VARCHAR(16),
    recipient        VARCHAR(16),
    atm_name         VARCHAR(25),
    FOREIGN KEY (sender) REFERENCES atm_accounts (card_number),
    FOREIGN KEY (recipient) REFERENCES atm_accounts (card_number),
    FOREIGN KEY (atm_name) REFERENCES atms (name)
);

