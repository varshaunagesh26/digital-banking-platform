ALTER TABLE account
ADD CONSTRAINT account_balance_min_value CHECK (account_balance>=1000);