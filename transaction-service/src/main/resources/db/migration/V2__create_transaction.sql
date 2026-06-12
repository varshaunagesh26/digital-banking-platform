CREATE TABLE transaction.transaction(
id serial PRIMARY KEY,
transaction_number varchar(225) NOT NULL,
from_account_number bigint,
to_account_number bigint,
amount decimal(10,2) NOT NULL,
currency varchar(10),
type varchar(20),
transaction_status varchar(20) NOT NULL,
reference_number varchar(30) NOT NULL,
time_stamp date default '01-01-1900',
idempotency_key varchar(20),
created_at TIMESTAMP WITHOUT TIME ZONE,
created_by varchar(50)
);