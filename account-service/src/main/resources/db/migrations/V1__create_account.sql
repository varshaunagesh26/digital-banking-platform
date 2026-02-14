CREATE TABLE account(
id bigint PRIMARY KEY,
account_number bigint NOT NULL,
account_type varchar(20) NOT NULL,
account_balance decimal(10,2) NOT NULL
);