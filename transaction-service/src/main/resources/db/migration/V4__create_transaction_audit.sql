CREATE TABLE transaction.transaction_audit(
id bigint,
transaction_number bigint,
from_account_number bigint,
to_account_number bigint,
amount decimal(10,2),
currency varchar(10),
type varchar(20),
transaction_status varchar(20),
reference_number varchar(30),
time_stamp date default '01-01-1900',
idempotency_key varchar(20),
created_at TIMESTAMP WITHOUT TIME ZONE,
created_by varchar(50)
);