ALTER TABLE account
ADD COLUMN customer_id bigint;

ALTER TABLE account
ADD CONSTRAINT fk_account_customer
FOREIGN KEY(customer_id)
REFERENCES customer(id);

ALTER TABLE account
ADD COLUMN branch_id bigint;

ALTER TABLE account
ADD CONSTRAINT fk_account_branch
FOREIGN KEY(branch_id)
REFERENCES branch(id);
