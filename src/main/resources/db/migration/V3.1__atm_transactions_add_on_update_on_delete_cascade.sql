ALTER TABLE IF EXISTS atm_transactions
    DROP CONSTRAINT atm_transactions_sender_fkey;

ALTER TABLE IF EXISTS atm_transactions
    ADD CONSTRAINT atm_transactions_sender_fkey
        FOREIGN KEY (sender)
            REFERENCES atm_accounts (card_number)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

ALTER TABLE IF EXISTS atm_transactions
    DROP CONSTRAINT atm_transactions_recipient_fkey;

ALTER TABLE IF EXISTS atm_transactions
    ADD CONSTRAINT atm_transactions_recipient_fkey
        FOREIGN KEY (recipient)
            REFERENCES atm_accounts (card_number)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

ALTER TABLE IF EXISTS atm_transactions
    DROP CONSTRAINT atm_transactions_atm_name_fkey;

ALTER TABLE IF EXISTS atm_transactions
    ADD CONSTRAINT atm_transactions_atm_name_fkey
        FOREIGN KEY (atm_name)
            REFERENCES atms (name)
            ON DELETE CASCADE
            ON UPDATE CASCADE;