CREATE TABLE IF NOT EXISTS atm_transactions
(
    id                    BIGSERIAL PRIMARY KEY,
    transaction_type      VARCHAR(10) NOT NULL,
    amount                BIGINT      NOT NULL,
    timestamp             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sender_card_number    VARCHAR(16) NOT NULL,
    recipient_card_number VARCHAR(16) NOT NULL,
    FOREIGN KEY (sender_card_number) REFERENCES atm_accounts (card_number),
    FOREIGN KEY (recipient_card_number) REFERENCES atm_accounts (card_number)
);

