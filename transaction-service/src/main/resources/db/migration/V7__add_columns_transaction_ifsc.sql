ALTER TABLE transaction.transaction
ADD COLUMN IF NOT EXISTS from_ifsc_code varchar(100),
ADD COLUMN IF NOT EXISTS to_ifsc_code varchar(100);