CREATE TABLE accounts (
                          account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          customer_id UUID NOT NULL,
                          account_name VARCHAR(255),
                          account_number INTEGER,
                          balance NUMERIC(19, 4) NOT NULL,
                          account_type VARCHAR(50),
                          status VARCHAR(50),
                          overdraft_protection BOOLEAN,
                          transaction_limit NUMERIC(19, 4) NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          version INTEGER,
                          CONSTRAINT chk_balance_positive CHECK (balance >= 0),
                          CONSTRAINT chk_transaction_limit_positive CHECK (transaction_limit >= 0)
);

CREATE TABLE account_holders (
                                 account_id UUID NOT NULL,
                                 customer_id UUID NOT NULL,
                                 PRIMARY KEY (account_id, customer_id),
                                 CONSTRAINT fk_account_holders_account FOREIGN KEY (account_id)
                                     REFERENCES accounts (account_id)
                                     ON DELETE CASCADE
);
