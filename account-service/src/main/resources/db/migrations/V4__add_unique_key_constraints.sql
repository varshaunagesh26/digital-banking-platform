ALTER TABLE account
ADD CONSTRAINT uc_account_account_number UNIQUE (account_number);

ALTER TABLE branch
ADD CONSTRAINT uc_branch_branch_code UNIQUE (branch_code);

ALTER TABLE branch
ADD CONSTRAINT uc_branch_branch_ifsc UNIQUE (branch_ifsc);

ALTER TABLE customer
ADD CONSTRAINT uc_customer_customer_number UNIQUE (customer_id);