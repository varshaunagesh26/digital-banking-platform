ALTER TABLE transaction.transaction
    ALTER COLUMN reference_number TYPE VARCHAR(255),
    ALTER COLUMN idempotency_key TYPE VARCHAR(255),
    ALTER COLUMN created_by TYPE VARCHAR(100)