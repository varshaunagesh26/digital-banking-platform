-- Existing templates adjusted for sequential key consistency
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6054, 'savings', 64250.00, 4, 6, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6053, 'current', 101500.00, 2, 10, true);

-- Inactive Customers (Exactly 4 accounts mapped to IDs: 1, 13, 18, 28)
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6005, 'savings', 4500.00, 1, 1, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6006, 'current', 78200.00, 13, 2, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6007, 'savings', 12500.00, 18, 3, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6008, 'savings', 95000.00, 20, 4, true);

-- Remaining Inactive Accounts (6 more to hit the total of 8 'is_active = false' accounts)
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6009, 'savings', 15000.00, 2, 5, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6010, 'current', 220000.00, 3, 6, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6011, 'savings', 35000.00, 4, 7, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6012, 'current', 89000.00, 6, 8, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6013, 'savings', 43000.00, 7, 9, false);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6014, 'savings', 12500.00, 8, 10, false);

-- Active Accounts for Active Customers (Remaining 36 accounts)
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6015, 'current', 145000.00, 9, 4, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6016, 'savings', 67000.00, 10, 1, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6017, 'savings', 8900.00, 11, 2, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6018, 'current', 310000.00, 12, 3, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6019, 'savings', 54000.00, 14, 4, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6020, 'savings', 23000.00, 15, 5, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6021, 'current', 115000.00, 16, 6, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6022, 'savings', 74000.00, 17, 7, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6023, 'savings', 62000.00, 19, 8, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6024, 'current', 105000.00, 20, 9, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6025, 'savings', 99000.00, 21, 10, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6026, 'savings', 14000.00, 22, 6, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6027, 'current', 250000.00, 23, 1, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6028, 'savings', 33000.00, 24, 2, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6029, 'savings', 41000.00, 15, 3, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6030, 'current', 185000.00, 22, 4, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6031, 'savings', 52000.00, 23, 5, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6032, 'savings', 61000.00, 24, 6, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6033, 'current', 123000.00, 18, 7, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6034, 'savings', 47000.00, 2, 8, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6035, 'savings', 83000.00, 3, 9, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6036, 'current', 92000.00, 4, 10, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6037, 'savings', 104000.00, 6, 8, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6038, 'savings', 15000.00, 7, 1, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6039, 'current', 210000.00, 8, 2, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6040, 'savings', 59000.00, 9, 3, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6041, 'savings', 72000.00, 10, 4, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6042, 'current', 133000.00, 11, 5, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6043, 'savings', 26000.00, 12, 6, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6044, 'savings', 68000.00, 14, 7, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6045, 'current', 174000.00, 15, 8, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6046, 'savings', 81000.00, 16, 9, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6047, 'savings', 93000.00, 17, 10, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6048, 'current', 142000.00, 19, 10, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6049, 'savings', 38000.00, 20, 1, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6050, 'savings', 49000.00, 21, 2, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6051, 'current', 215000.00, 22, 3, true);
INSERT INTO account.account (account_number, account_type, account_balance, customer_id, branch_id, is_active) VALUES(6052, 'savings', 86000.00, 23, 4, true);