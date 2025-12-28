CREATE TABLE transfers (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    from_wallet_id CHAR(36) NOT NULL,
    to_wallet_id CHAR(36) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_from FOREIGN KEY (from_wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_transfer_to FOREIGN KEY (to_wallet_id) REFERENCES wallets(id)
);
