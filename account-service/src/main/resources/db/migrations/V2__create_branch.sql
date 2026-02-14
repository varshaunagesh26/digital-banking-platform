CREATE TABLE branch(
id bigint PRIMARY KEY,
branch_code bigint NOT NULL,
branch_name varchar(50) NOT NULL,
branch_address varchar(255) NOT NULL,
branch_ifsc varchar(12) NOT NULL
);