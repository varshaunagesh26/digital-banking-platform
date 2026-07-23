CREATE TABLE customer(
id serial PRIMARY KEY,
customer_id bigint NOT NULL,
first_name varchar(30) NOT NULL,
last_name varchar(30),
customer_dob date default '01-01-1900',
phone bigint NOT NULL,
customer_email varchar(20),
customer_address varchar(255)
);