CREATE TABLE IF NOT EXISTS atm_accounts (
                                          id BIGSERIAL PRIMARY KEY,
                                          card_number VARCHAR(16) UNIQUE NOT NULL,
                                          pin_number VARCHAR(4) NOT NULL,
                                          balance BIGINT NOT NULL
);

