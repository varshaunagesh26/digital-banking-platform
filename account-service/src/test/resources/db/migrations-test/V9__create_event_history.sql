CREATE TABLE event_history(
id serial PRIMARY KEY,
transaction_number varchar(255) NOT NULL,
transaction_status varchar(20) NOT NULL,
event_status varchar(20) NOT NULL,
transaction_type varchar(20) NOT NULL
)