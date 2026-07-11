CREATE TABLE payment.payment(
id serial PRIMARY KEY,
payment_id varchar(255) NOT NULL UNIQUE,
from_account_number varchar(100) NOT NULL,
from_ifsc_code varchar(255) NOT NULL,
to_account_number varchar(100),
to_ifsc_code varchar(255),
amount decimal(10,2),
payment_type varchar(100) NOT NULL,
payment_operation varchar(100) NOT NULL,
payment_status varchar(100) NOT NULL
);