CREATE TABLE transactions (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    wallet_id CHAR(36) NOT NULL,
    type ENUM('CREDIT','DEBIT') NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
    reference_id VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);
