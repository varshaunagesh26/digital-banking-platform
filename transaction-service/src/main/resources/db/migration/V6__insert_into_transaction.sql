-- =========================================================================
-- TRANSACTIONS FOR INACTIVE ACCOUNTS (isActive = false -> Accounts 6003, 6054)
-- Requirement: Around 8 transactions total, resulting in FAILED status.
-- =========================================================================

-- Account 6003 (4 Transactions)
INSERT INTO "transaction"."transaction" (transaction_number, from_account_number, to_account_number, amount, currency, "type", transaction_status, reference_number, "time_stamp", idempotency_key, created_at, created_by) VALUES
('b301a5d0-9f20-4e0d-b108-7ae1f7cbb101', NULL, 6003, 1500.0, 'USD', 'DEPOSIT', 'OPENED', 'b301a5d0-9f20-4e0d-b108-7ae1f7cbb101', '2026-06-11 09:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b302a5d0-9f20-4e0d-b108-7ae1f7cbb102', 6003, NULL, 500.0, 'INR', 'WITHDRAW', 'OPENED', 'b302a5d0-9f20-4e0d-b108-7ae1f7cbb102', '2026-06-11 09:15:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b303a5d0-9f20-4e0d-b108-7ae1f7cbb103', 6001, 6003, 2000.0, 'CHF', 'TRANSFER', 'OPENED', 'b303a5d0-9f20-4e0d-b108-7ae1f7cbb103', '2026-06-11 09:30:00.000', NULL, NULL, 'transaction-service-transfer'),
('b304a5d0-9f20-4e0d-b108-7ae1f7cbb104', 6003, 6002, 1200.0, 'EURO', 'TRANSFER', 'OPENED', 'b304a5d0-9f20-4e0d-b108-7ae1f7cbb104', '2026-06-11 09:45:00.000', NULL, NULL, 'transaction-service-transfer');

-- Account 6054 (4 Transactions)
INSERT INTO "transaction"."transaction" (transaction_number, from_account_number, to_account_number, amount, currency, "type", transaction_status, reference_number, "time_stamp", idempotency_key, created_at, created_by) VALUES
('b305a5d0-9f20-4e0d-b108-7ae1f7cbb105', NULL, 6054, 3000.0, 'INR', 'DEPOSIT', 'OPENED', 'b305a5d0-9f20-4e0d-b108-7ae1f7cbb105', '2026-06-11 10:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b306a5d0-9f20-4e0d-b108-7ae1f7cbb106', 6054, NULL, 1000.0, 'EURO', 'WITHDRAW', 'OPENED', 'b306a5d0-9f20-4e0d-b108-7ae1f7cbb106', '2026-06-11 10:15:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b307a5d0-9f20-4e0d-b108-7ae1f7cbb107', 6004, 6054, 4500.0, 'USD', 'TRANSFER', 'OPENED', 'b307a5d0-9f20-4e0d-b108-7ae1f7cbb107', '2026-06-11 10:30:00.000', NULL, NULL, 'transaction-service-transfer'),
('b308a5d0-9f20-4e0d-b108-7ae1f7cbb108', 6054, 6005, 800.0, 'CHF', 'TRANSFER', 'OPENED', 'b308a5d0-9f20-4e0d-b108-7ae1f7cbb108', '2026-06-11 10:45:00.000', NULL, NULL, 'transaction-service-transfer');


-- =========================================================================
-- FEW COMPLETED & FAILED TRANSACTIONS FOR ACTIVE ACCOUNTS
-- =========================================================================

INSERT INTO "transaction"."transaction" (transaction_number, from_account_number, to_account_number, amount, currency, "type", transaction_status, reference_number, "time_stamp", idempotency_key, created_at, created_by) VALUES
('b309a5d0-9f20-4e0d-b108-7ae1f7cbb109', NULL, 6001, 10000.0, 'USD', 'DEPOSIT', 'COMPLETED', 'b309a5d0-9f20-4e0d-b108-7ae1f7cbb109', '2026-06-11 11:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b310a5d0-9f20-4e0d-b108-7ae1f7cbb110', 6002, NULL, 5000.0, 'EURO', 'WITHDRAW', 'COMPLETED', 'b310a5d0-9f20-4e0d-b108-7ae1f7cbb110', '2026-06-11 11:15:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b311a5d0-9f20-4e0d-b108-7ae1f7cbb111', 6004, 6002, 15000.0, 'INR', 'TRANSFER', 'COMPLETED', 'b311a5d0-9f20-4e0d-b108-7ae1f7cbb111', '2026-06-11 11:30:00.000', NULL, NULL, 'transaction-service-transfer'),
('b312a5d0-9f20-4e0d-b108-7ae1f7cbb112', 6053, 6006, 2500.0, 'CHF', 'TRANSFER', 'COMPLETED', 'b312a5d0-9f20-4e0d-b108-7ae1f7cbb112', '2026-06-11 11:45:00.000', NULL, NULL, 'transaction-service-transfer'),
('b313a5d0-9f20-4e0d-b108-7ae1f7cbb113', NULL, 6007, 50000.0, 'USD', 'DEPOSIT', 'OPENED', 'b313a5d0-9f20-4e0d-b108-7ae1f7cbb113', '2026-06-11 12:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b314a5d0-9f20-4e0d-b108-7ae1f7cbb114', 6008, NULL, 99999.0, 'INR', 'WITHDRAW', 'OPENED', 'b314a5d0-9f20-4e0d-b108-7ae1f7cbb114', '2026-06-11 12:15:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b315a5d0-9f20-4e0d-b108-7ae1f7cbb115', 6009, 6010, 4000.0, 'EURO', 'TRANSFER', 'OPENED', 'b315a5d0-9f20-4e0d-b108-7ae1f7cbb115', '2026-06-11 12:30:00.000', NULL, NULL, 'transaction-service-transfer');


-- =========================================================================
-- BULK TRANSACTIONS: INPROGRESS STATUS (40 Statements)
-- =========================================================================

INSERT INTO "transaction"."transaction" (transaction_number, from_account_number, to_account_number, amount, currency, "type", transaction_status, reference_number, "time_stamp", idempotency_key, created_at, created_by) VALUES
('b316a5d0-9f20-4e0d-b108-7ae1f7cbb116', NULL, 6011, 4500.0, 'CHF', 'DEPOSIT', 'INPROGRESS', 'b316a5d0-9f20-4e0d-b108-7ae1f7cbb116', '2026-06-12 01:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b317a5d0-9f20-4e0d-b108-7ae1f7cbb117', 6012, NULL, 2300.0, 'USD', 'WITHDRAW', 'INPROGRESS', 'b317a5d0-9f20-4e0d-b108-7ae1f7cbb117', '2026-06-12 01:05:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b318a5d0-9f20-4e0d-b108-7ae1f7cbb118', 6013, 6014, 1500.0, 'EURO', 'TRANSFER', 'INPROGRESS', 'b318a5d0-9f20-4e0d-b108-7ae1f7cbb118', '2026-06-12 01:10:00.000', NULL, NULL, 'transaction-service-transfer'),
('b319a5d0-9f20-4e0d-b108-7ae1f7cbb119', NULL, 6015, 8900.0, 'INR', 'DEPOSIT', 'INPROGRESS', 'b319a5d0-9f20-4e0d-b108-7ae1f7cbb119', '2026-06-12 01:15:00.000', NULL, NULL, 'transaction-service-deposit'),
('b320a5d0-9f20-4e0d-b108-7ae1f7cbb120', 6016, NULL, 3100.0, 'CHF', 'WITHDRAW', 'INPROGRESS', 'b320a5d0-9f20-4e0d-b108-7ae1f7cbb120', '2026-06-12 01:20:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b321a5d0-9f20-4e0d-b108-7ae1f7cbb121', 6017, 6018, 6200.0, 'USD', 'TRANSFER', 'INPROGRESS', 'b321a5d0-9f20-4e0d-b108-7ae1f7cbb121', '2026-06-12 01:25:00.000', NULL, NULL, 'transaction-service-transfer'),
('b322a5d0-9f20-4e0d-b108-7ae1f7cbb122', NULL, 6019, 12000.0, 'EURO', 'DEPOSIT', 'INPROGRESS', 'b322a5d0-9f20-4e0d-b108-7ae1f7cbb122', '2026-06-12 01:30:00.000', NULL, NULL, 'transaction-service-deposit'),
('b323a5d0-9f20-4e0d-b108-7ae1f7cbb123', 6020, NULL, 450.0, 'INR', 'WITHDRAW', 'INPROGRESS', 'b323a5d0-9f20-4e0d-b108-7ae1f7cbb123', '2026-06-12 01:35:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b324a5d0-9f20-4e0d-b108-7ae1f7cbb124', 6021, 6022, 10500.0, 'USD', 'TRANSFER', 'INPROGRESS', 'b324a5d0-9f20-4e0d-b108-7ae1f7cbb124', '2026-06-12 01:40:00.000', NULL, NULL, 'transaction-service-transfer'),
('b325a5d0-9f20-4e0d-b108-7ae1f7cbb125', NULL, 6023, 7300.0, 'CHF', 'DEPOSIT', 'INPROGRESS', 'b325a5d0-9f20-4e0d-b108-7ae1f7cbb125', '2026-06-12 01:45:00.000', NULL, NULL, 'transaction-service-deposit'),
('b326a5d0-9f20-4e0d-b108-7ae1f7cbb126', 6024, NULL, 2100.0, 'EURO', 'WITHDRAW', 'INPROGRESS', 'b326a5d0-9f20-4e0d-b108-7ae1f7cbb126', '2026-06-12 01:50:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b327a5d0-9f20-4e0d-b108-7ae1f7cbb127', 6025, 6026, 3400.0, 'INR', 'TRANSFER', 'INPROGRESS', 'b327a5d0-9f20-4e0d-b108-7ae1f7cbb127', '2026-06-12 01:55:00.000', NULL, NULL, 'transaction-service-transfer'),
('b328a5d0-9f20-4e0d-b108-7ae1f7cbb128', NULL, 6027, 25000.0, 'USD', 'DEPOSIT', 'INPROGRESS', 'b328a5d0-9f20-4e0d-b108-7ae1f7cbb128', '2026-06-12 02:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b329a5d0-9f20-4e0d-b108-7ae1f7cbb129', 6028, NULL, 1100.0, 'CHF', 'WITHDRAW', 'INPROGRESS', 'b329a5d0-9f20-4e0d-b108-7ae1f7cbb129', '2026-06-12 02:05:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b330a5d0-9f20-4e0d-b108-7ae1f7cbb130', 6029, 6030, 8900.0, 'EURO', 'TRANSFER', 'INPROGRESS', 'b330a5d0-9f20-4e0d-b108-7ae1f7cbb130', '2026-06-12 02:10:00.000', NULL, NULL, 'transaction-service-transfer'),
('b331a5d0-9f20-4e0d-b108-7ae1f7cbb131', NULL, 6031, 5600.0, 'INR', 'DEPOSIT', 'INPROGRESS', 'b331a5d0-9f20-4e0d-b108-7ae1f7cbb131', '2026-06-12 02:15:00.000', NULL, NULL, 'transaction-service-deposit'),
('b332a5d0-9f20-4e0d-b108-7ae1f7cbb132', 6032, NULL, 4000.0, 'USD', 'WITHDRAW', 'INPROGRESS', 'b332a5d0-9f20-4e0d-b108-7ae1f7cbb132', '2026-06-12 02:20:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b333a5d0-9f20-4e0d-b108-7ae1f7cbb133', 6033, 6034, 12000.0, 'CHF', 'TRANSFER', 'INPROGRESS', 'b333a5d0-9f20-4e0d-b108-7ae1f7cbb133', '2026-06-12 02:25:00.000', NULL, NULL, 'transaction-service-transfer'),
('b334a5d0-9f20-4e0d-b108-7ae1f7cbb134', NULL, 6035, 17500.0, 'EURO', 'DEPOSIT', 'INPROGRESS', 'b334a5d0-9f20-4e0d-b108-7ae1f7cbb134', '2026-06-12 02:30:00.000', NULL, NULL, 'transaction-service-deposit'),
('b335a5d0-9f20-4e0d-b108-7ae1f7cbb135', 6036, NULL, 900.0, 'INR', 'WITHDRAW', 'INPROGRESS', 'b335a5d0-9f20-4e0d-b108-7ae1f7cbb135', '2026-06-12 02:35:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b336a5d0-9f20-4e0d-b108-7ae1f7cbb136', 6037, 6038, 3100.0, 'CHF', 'TRANSFER', 'INPROGRESS', 'b336a5d0-9f20-4e0d-b108-7ae1f7cbb136', '2026-06-12 02:40:00.000', NULL, NULL, 'transaction-service-transfer'),
('b337a5d0-9f20-4e0d-b108-7ae1f7cbb137', NULL, 6039, 22000.0, 'USD', 'DEPOSIT', 'INPROGRESS', 'b337a5d0-9f20-4e0d-b108-7ae1f7cbb137', '2026-06-12 02:45:00.000', NULL, NULL, 'transaction-service-deposit'),
('b338a5d0-9f20-4e0d-b108-7ae1f7cbb138', 6040, NULL, 6100.0, 'EURO', 'WITHDRAW', 'INPROGRESS', 'b338a5d0-9f20-4e0d-b108-7ae1f7cbb138', '2026-06-12 02:50:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b339a5d0-9f20-4e0d-b108-7ae1f7cbb139', 6041, 6042, 14000.0, 'INR', 'TRANSFER', 'INPROGRESS', 'b339a5d0-9f20-4e0d-b108-7ae1f7cbb139', '2026-06-12 02:55:00.000', NULL, NULL, 'transaction-service-transfer'),
('b340a5d0-9f20-4e0d-b108-7ae1f7cbb140', NULL, 6043, 1900.0, 'USD', 'DEPOSIT', 'INPROGRESS', 'b340a5d0-9f20-4e0d-b108-7ae1f7cbb140', '2026-06-12 03:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b341a5d0-9f20-4e0d-b108-7ae1f7cbb141', 6044, NULL, 5300.0, 'CHF', 'WITHDRAW', 'INPROGRESS', 'b341a5d0-9f20-4e0d-b108-7ae1f7cbb141', '2026-06-12 03:05:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b342a5d0-9f20-4e0d-b108-7ae1f7cbb142', 6045, 6046, 26000.0, 'EURO', 'TRANSFER', 'INPROGRESS', 'b342a5d0-9f20-4e0d-b108-7ae1f7cbb142', '2026-06-12 03:10:00.000', NULL, NULL, 'transaction-service-transfer'),
('b343a5d0-9f20-4e0d-b108-7ae1f7cbb143', NULL, 6047, 8400.0, 'INR', 'DEPOSIT', 'INPROGRESS', 'b343a5d0-9f20-4e0d-b108-7ae1f7cbb143', '2026-06-12 03:15:00.000', NULL, NULL, 'transaction-service-deposit'),
('b344a5d0-9f20-4e0d-b108-7ae1f7cbb144', 6048, NULL, 12000.0, 'CHF', 'WITHDRAW', 'INPROGRESS', 'b344a5d0-9f20-4e0d-b108-7ae1f7cbb144', '2026-06-12 03:20:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b345a5d0-9f20-4e0d-b108-7ae1f7cbb145', 6049, 6050, 4100.0, 'USD', 'TRANSFER', 'INPROGRESS', 'b345a5d0-9f20-4e0d-b108-7ae1f7cbb145', '2026-06-12 03:25:00.000', NULL, NULL, 'transaction-service-transfer'),
('b346a5d0-9f20-4e0d-b108-7ae1f7cbb146', NULL, 6051, 33000.0, 'EURO', 'DEPOSIT', 'INPROGRESS', 'b346a5d0-9f20-4e0d-b108-7ae1f7cbb146', '2026-06-12 03:30:00.000', NULL, NULL, 'transaction-service-deposit'),
('b347a5d0-9f20-4e0d-b108-7ae1f7cbb147', 6052, NULL, 7200.0, 'INR', 'WITHDRAW', 'INPROGRESS', 'b347a5d0-9f20-4e0d-b108-7ae1f7cbb147', '2026-06-12 03:35:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b348a5d0-9f20-4e0d-b108-7ae1f7cbb148', 6001, 6053, 9500.0, 'USD', 'TRANSFER', 'INPROGRESS', 'b348a5d0-9f20-4e0d-b108-7ae1f7cbb148', '2026-06-12 03:40:00.000', NULL, NULL, 'transaction-service-transfer'),
('b349a5d0-9f20-4e0d-b108-7ae1f7cbb149', NULL, 6002, 11500.0, 'CHF', 'DEPOSIT', 'INPROGRESS', 'b349a5d0-9f20-4e0d-b108-7ae1f7cbb149', '2026-06-12 03:45:00.000', NULL, NULL, 'transaction-service-deposit'),
('b350a5d0-9f20-4e0d-b108-7ae1f7cbb150', 6004, NULL, 6400.0, 'EURO', 'WITHDRAW', 'INPROGRESS', 'b350a5d0-9f20-4e0d-b108-7ae1f7cbb150', '2026-06-12 03:50:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b351a5d0-9f20-4e0d-b108-7ae1f7cbb151', 6005, 6006, 1200.0, 'INR', 'TRANSFER', 'INPROGRESS', 'b351a5d0-9f20-4e0d-b108-7ae1f7cbb151', '2026-06-12 03:55:00.000', NULL, NULL, 'transaction-service-transfer'),
('b352a5d0-9f20-4e0d-b108-7ae1f7cbb152', NULL, 6007, 3000.0, 'CHF', 'DEPOSIT', 'INPROGRESS', 'b352a5d0-9f20-4e0d-b108-7ae1f7cbb152', '2026-06-12 04:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b353a5d0-9f20-4e0d-b108-7ae1f7cbb153', 6008, NULL, 15000.0, 'USD', 'WITHDRAW', 'INPROGRESS', 'b353a5d0-9f20-4e0d-b108-7ae1f7cbb153', '2026-06-12 04:05:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b354a5d0-9f20-4e0d-b108-7ae1f7cbb154', 6009, 6011, 2200.0, 'EURO', 'TRANSFER', 'INPROGRESS', 'b354a5d0-9f20-4e0d-b108-7ae1f7cbb154', '2026-06-12 04:10:00.000', NULL, NULL, 'transaction-service-transfer'),
('b355a5d0-9f20-4e0d-b108-7ae1f7cbb155', NULL, 6010, 45000.0, 'INR', 'DEPOSIT', 'INPROGRESS', 'b355a5d0-9f20-4e0d-b108-7ae1f7cbb155', '2026-06-12 04:15:00.000', NULL, NULL, 'transaction-service-deposit');


-- =========================================================================
-- BULK TRANSACTIONS: OPENED STATUS (53 Statements)
-- =========================================================================

INSERT INTO "transaction"."transaction" (transaction_number, from_account_number, to_account_number, amount, currency, "type", transaction_status, reference_number, "time_stamp", idempotency_key, created_at, created_by) VALUES
('b356a5d0-9f20-4e0d-b108-7ae1f7cbb156', 6012, NULL, 8500.0, 'USD', 'WITHDRAW', 'OPENED', 'b356a5d0-9f20-4e0d-b108-7ae1f7cbb156', '2026-06-12 05:00:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b357a5d0-9f20-4e0d-b108-7ae1f7cbb157', 6013, 6015, 3400.0, 'CHF', 'TRANSFER', 'OPENED', 'b357a5d0-9f20-4e0d-b108-7ae1f7cbb157', '2026-06-12 05:03:00.000', NULL, NULL, 'transaction-service-transfer'),
('b358a5d0-9f20-4e0d-b108-7ae1f7cbb158', NULL, 6014, 1100.0, 'EURO', 'DEPOSIT', 'OPENED', 'b358a5d0-9f20-4e0d-b108-7ae1f7cbb158', '2026-06-12 05:06:00.000', NULL, NULL, 'transaction-service-deposit'),
('b359a5d0-9f20-4e0d-b108-7ae1f7cbb159', 6016, NULL, 5000.0, 'INR', 'WITHDRAW', 'OPENED', 'b359a5d0-9f20-4e0d-b108-7ae1f7cbb159', '2026-06-12 05:09:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b360a5d0-9f20-4e0d-b108-7ae1f7cbb160', 6017, 6019, 1400.0, 'EURO', 'TRANSFER', 'OPENED', 'b360a5d0-9f20-4e0d-b108-7ae1f7cbb160', '2026-06-12 05:12:00.000', NULL, NULL, 'transaction-service-transfer'),
('b361a5d0-9f20-4e0d-b108-7ae1f7cbb161', NULL, 6018, 95000.0, 'INR', 'DEPOSIT', 'OPENED', 'b361a5d0-9f20-4e0d-b108-7ae1f7cbb161', '2026-06-12 05:15:00.000', NULL, NULL, 'transaction-service-deposit'),
('b362a5d0-9f20-4e0d-b108-7ae1f7cbb162', 6020, NULL, 2300.0, 'USD', 'WITHDRAW', 'OPENED', 'b362a5d0-9f20-4e0d-b108-7ae1f7cbb162', '2026-06-12 05:18:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b363a5d0-9f20-4e0d-b108-7ae1f7cbb163', 6021, 6023, 14500.0, 'CHF', 'TRANSFER', 'OPENED', 'b363a5d0-9f20-4e0d-b108-7ae1f7cbb163', '2026-06-12 05:21:00.000', NULL, NULL, 'transaction-service-transfer'),
('b364a5d0-9f20-4e0d-b108-7ae1f7cbb164', NULL, 6022, 6000.0, 'EURO', 'DEPOSIT', 'OPENED', 'b364a5d0-9f20-4e0d-b108-7ae1f7cbb164', '2026-06-12 05:24:00.000', NULL, NULL, 'transaction-service-deposit'),
('b365a5d0-9f20-4e0d-b108-7ae1f7cbb165', 6024, NULL, 10000.0, 'INR', 'WITHDRAW', 'OPENED', 'b365a5d0-9f20-4e0d-b108-7ae1f7cbb165', '2026-06-12 05:27:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b366a5d0-9f20-4e0d-b108-7ae1f7cbb166', 6025, 6027, 8800.0, 'USD', 'TRANSFER', 'OPENED', 'b366a5d0-9f20-4e0d-b108-7ae1f7cbb166', '2026-06-12 05:30:00.000', NULL, NULL, 'transaction-service-transfer'),
('b367a5d0-9f20-4e0d-b108-7ae1f7cbb167', NULL, 6026, 3300.0, 'CHF', 'DEPOSIT', 'OPENED', 'b367a5d0-9f20-4e0d-b108-7ae1f7cbb167', '2026-06-12 05:33:00.000', NULL, NULL, 'transaction-service-deposit'),
('b368a5d0-9f20-4e0d-b108-7ae1f7cbb168', 6028, NULL, 4200.0, 'EURO', 'WITHDRAW', 'OPENED', 'b368a5d0-9f20-4e0d-b108-7ae1f7cbb168', '2026-06-12 05:36:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b369a5d0-9f20-4e0d-b108-7ae1f7cbb169', 6029, 6031, 5100.0, 'INR', 'TRANSFER', 'OPENED', 'b369a5d0-9f20-4e0d-b108-7ae1f7cbb169', '2026-06-12 05:39:00.000', NULL, NULL, 'transaction-service-transfer'),
('b370a5d0-9f20-4e0d-b108-7ae1f7cbb170', NULL, 6030, 22000.0, 'USD', 'DEPOSIT', 'OPENED', 'b370a5d0-9f20-4e0d-b108-7ae1f7cbb170', '2026-06-12 05:42:00.000', NULL, NULL, 'transaction-service-deposit'),
('b371a5d0-9f20-4e0d-b108-7ae1f7cbb171', 6032, NULL, 7000.0, 'CHF', 'WITHDRAW', 'OPENED', 'b371a5d0-9f20-4e0d-b108-7ae1f7cbb171', '2026-06-12 05:45:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b372a5d0-9f20-4e0d-b108-7ae1f7cbb172', 6033, 6035, 13500.0, 'EURO', 'TRANSFER', 'OPENED', 'b372a5d0-9f20-4e0d-b108-7ae1f7cbb172', '2026-06-12 05:48:00.000', NULL, NULL, 'transaction-service-transfer'),
('b373a5d0-9f20-4e0d-b108-7ae1f7cbb173', NULL, 6034, 4100.0, 'INR', 'DEPOSIT', 'OPENED', 'b373a5d0-9f20-4e0d-b108-7ae1f7cbb173', '2026-06-12 05:51:00.000', NULL, NULL, 'transaction-service-deposit'),
('b374a5d0-9f20-4e0d-b108-7ae1f7cbb174', 6036, NULL, 8000.0, 'USD', 'WITHDRAW', 'OPENED', 'b374a5d0-9f20-4e0d-b108-7ae1f7cbb174', '2026-06-12 05:54:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b375a5d0-9f20-4e0d-b108-7ae1f7cbb175', 6037, 6039, 19000.0, 'CHF', 'TRANSFER', 'OPENED', 'b375a5d0-9f20-4e0d-b108-7ae1f7cbb175', '2026-06-12 05:57:00.000', NULL, NULL, 'transaction-service-transfer'),
('b376a5d0-9f20-4e0d-b108-7ae1f7cbb176', NULL, 6038, 2500.0, 'EURO', 'DEPOSIT', 'OPENED', 'b376a5d0-9f20-4e0d-b108-7ae1f7cbb176', '2026-06-12 06:00:00.000', NULL, NULL, 'transaction-service-deposit'),
('b377a5d0-9f20-4e0d-b108-7ae1f7cbb177', 6040, NULL, 3400.0, 'INR', 'WITHDRAW', 'OPENED', 'b377a5d0-9f20-4e0d-b108-7ae1f7cbb177', '2026-06-12 06:03:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b378a5d0-9f20-4e0d-b108-7ae1f7cbb178', 6041, 6043, 6600.0, 'USD', 'TRANSFER', 'OPENED', 'b378a5d0-9f20-4e0d-b108-7ae1f7cbb178', '2026-06-12 06:06:00.000', NULL, NULL, 'transaction-service-transfer'),
('b379a5d0-9f20-4e0d-b108-7ae1f7cbb179', NULL, 6042, 14000.0, 'CHF', 'DEPOSIT', 'OPENED', 'b379a5d0-9f20-4e0d-b108-7ae1f7cbb179', '2026-06-12 06:09:00.000', NULL, NULL, 'transaction-service-deposit'),
('b380a5d0-9f20-4e0d-b108-7ae1f7cbb180', 6044, NULL, 9000.0, 'EURO', 'WITHDRAW', 'OPENED', 'b380a5d0-9f20-4e0d-b108-7ae1f7cbb180', '2026-06-12 06:12:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b381a5d0-9f20-4e0d-b108-7ae1f7cbb181', 6045, 6047, 21000.0, 'INR', 'TRANSFER', 'OPENED', 'b381a5d0-9f20-4e0d-b108-7ae1f7cbb181', '2026-06-12 06:15:00.000', NULL, NULL, 'transaction-service-transfer'),
('b382a5d0-9f20-4e0d-b108-7ae1f7cbb182', NULL, 6046, 11500.0, 'USD', 'DEPOSIT', 'OPENED', 'b382a5d0-9f20-4e0d-b108-7ae1f7cbb182', '2026-06-12 06:18:00.000', NULL, NULL, 'transaction-service-deposit'),
('b383a5d0-9f20-4e0d-b108-7ae1f7cbb183', 6048, NULL, 13000.0, 'CHF', 'WITHDRAW', 'OPENED', 'b383a5d0-9f20-4e0d-b108-7ae1f7cbb183', '2026-06-12 06:21:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b384a5d0-9f20-4e0d-b108-7ae1f7cbb184', 6049, 6051, 5500.0, 'EURO', 'TRANSFER', 'OPENED', 'b384a5d0-9f20-4e0d-b108-7ae1f7cbb184', '2026-06-12 06:24:00.000', NULL, NULL, 'transaction-service-transfer'),
('b385a5d0-9f20-4e0d-b108-7ae1f7cbb185', NULL, 6050, 4800.0, 'INR', 'DEPOSIT', 'OPENED', 'b385a5d0-9f20-4e0d-b108-7ae1f7cbb185', '2026-06-12 06:27:00.000', NULL, NULL, 'transaction-service-deposit'),
('b386a5d0-9f20-4e0d-b108-7ae1f7cbb186', 6052, NULL, 6600.0, 'USD', 'WITHDRAW', 'OPENED', 'b386a5d0-9f20-4e0d-b108-7ae1f7cbb186', '2026-06-12 06:30:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b387a5d0-9f20-4e0d-b108-7ae1f7cbb187', 6053, 6001, 16000.0, 'CHF', 'TRANSFER', 'OPENED', 'b387a5d0-9f20-4e0d-b108-7ae1f7cbb187', '2026-06-12 06:33:00.000', NULL, NULL, 'transaction-service-transfer'),
('b388a5d0-9f20-4e0d-b108-7ae1f7cbb188', NULL, 6002, 2200.0, 'EURO', 'DEPOSIT', 'OPENED', 'b388a5d0-9f20-4e0d-b108-7ae1f7cbb188', '2026-06-12 06:36:00.000', NULL, NULL, 'transaction-service-deposit'),
('b389a5d0-9f20-4e0d-b108-7ae1f7cbb189', 6004, NULL, 31000.0, 'INR', 'WITHDRAW', 'OPENED', 'b389a5d0-9f20-4e0d-b108-7ae1f7cbb189', '2026-06-12 06:39:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b390a5d0-9f20-4e0d-b108-7ae1f7cbb190', 6005, 6007, 850.0, 'USD', 'TRANSFER', 'OPENED', 'b390a5d0-9f20-4e0d-b108-7ae1f7cbb190', '2026-06-12 06:42:00.000', NULL, NULL, 'transaction-service-transfer'),
('b391a5d0-9f20-4e0d-b108-7ae1f7cbb191', NULL, 6006, 12000.0, 'CHF', 'DEPOSIT', 'OPENED', 'b391a5d0-9f20-4e0d-b108-7ae1f7cbb191', '2026-06-12 06:45:00.000', NULL, NULL, 'transaction-service-deposit'),
('b392a5d0-9f20-4e0d-b108-7ae1f7cbb192', 6008, NULL, 4000.0, 'EURO', 'WITHDRAW', 'OPENED', 'b392a5d0-9f20-4e0d-b108-7ae1f7cbb192', '2026-06-12 06:48:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b393a5d0-9f20-4e0d-b108-7ae1f7cbb193', 6009, 6012, 1900.0, 'INR', 'TRANSFER', 'OPENED', 'b393a5d0-9f20-4e0d-b108-7ae1f7cbb193', '2026-06-12 06:51:00.000', NULL, NULL, 'transaction-service-transfer'),
('b394a5d0-9f20-4e0d-b108-7ae1f7cbb194', NULL, 6010, 15000.0, 'USD', 'DEPOSIT', 'OPENED', 'b394a5d0-9f20-4e0d-b108-7ae1f7cbb194', '2026-06-12 06:54:00.000', NULL, NULL, 'transaction-service-deposit'),
('b395a5d0-9f20-4e0d-b108-7ae1f7cbb195', 6011, NULL, 3000.0, 'CHF', 'WITHDRAW', 'OPENED', 'b395a5d0-9f20-4e0d-b108-7ae1f7cbb195', '2026-06-12 06:57:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b396a5d0-9f20-4e0d-b108-7ae1f7cbb196', 6013, 6016, 7400.0, 'EURO', 'TRANSFER', 'OPENED', 'b396a5d0-9f20-4e0d-b108-7ae1f7cbb196', '2026-06-12 07:00:00.000', NULL, NULL, 'transaction-service-transfer'),
('b397a5d0-9f20-4e0d-b108-7ae1f7cbb197', NULL, 6014, 2500.0, 'INR', 'DEPOSIT', 'OPENED', 'b397a5d0-9f20-4e0d-b108-7ae1f7cbb197', '2026-06-12 07:03:00.000', NULL, NULL, 'transaction-service-deposit'),
('b398a5d0-9f20-4e0d-b108-7ae1f7cbb198', 6015, NULL, 12500.0, 'USD', 'WITHDRAW', 'OPENED', 'b398a5d0-9f20-4e0d-b108-7ae1f7cbb198', '2026-06-12 07:06:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b399a5d0-9f20-4e0d-b108-7ae1f7cbb199', 6017, 6020, 2000.0, 'CHF', 'TRANSFER', 'OPENED', 'b399a5d0-9f20-4e0d-b108-7ae1f7cbb199', '2026-06-12 07:09:00.000', NULL, NULL, 'transaction-service-transfer'),
('b400a5d0-9f20-4e0d-b108-7ae1f7cbb200', NULL, 6018, 43000.0, 'EURO', 'DEPOSIT', 'OPENED', 'b400a5d0-9f20-4e0d-b108-7ae1f7cbb200', '2026-06-12 07:12:00.000', NULL, NULL, 'transaction-service-deposit'),
('b401a5d0-9f20-4e0d-b108-7ae1f7cbb201', 6019, NULL, 6000.0, 'INR', 'WITHDRAW', 'OPENED', 'b401a5d0-9f20-4e0d-b108-7ae1f7cbb201', '2026-06-12 07:15:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b402a5d0-9f20-4e0d-b108-7ae1f7cbb202', 6021, 6024, 18000.0, 'USD', 'TRANSFER', 'OPENED', 'b402a5d0-9f20-4e0d-b108-7ae1f7cbb202', '2026-06-12 07:18:00.000', NULL, NULL, 'transaction-service-transfer'),
('b403a5d0-9f20-4e0d-b108-7ae1f7cbb203', NULL, 6022, 9200.0, 'CHF', 'DEPOSIT', 'OPENED', 'b403a5d0-9f20-4e0d-b108-7ae1f7cbb203', '2026-06-12 07:21:00.000', NULL, NULL, 'transaction-service-deposit'),
('b404a5d0-9f20-4e0d-b108-7ae1f7cbb204', 6023, NULL, 4500.0, 'EURO', 'WITHDRAW', 'OPENED', 'b404a5d0-9f20-4e0d-b108-7ae1f7cbb204', '2026-06-12 07:24:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b405a5d0-9f20-4e0d-b108-7ae1f7cbb205', 6025, 6028, 12000.0, 'INR', 'TRANSFER', 'OPENED', 'b405a5d0-9f20-4e0d-b108-7ae1f7cbb205', '2026-06-12 07:27:00.000', NULL, NULL, 'transaction-service-transfer'),
('b406a5d0-9f20-4e0d-b108-7ae1f7cbb206', NULL, 6026, 1500.0, 'USD', 'DEPOSIT', 'OPENED', 'b406a5d0-9f20-4e0d-b108-7ae1f7cbb206', '2026-06-12 07:30:00.000', NULL, NULL, 'transaction-service-deposit'),
('b407a5d0-9f20-4e0d-b108-7ae1f7cbb207', 6027, NULL, 24000.0, 'CHF', 'WITHDRAW', 'OPENED', 'b407a5d0-9f20-4e0d-b108-7ae1f7cbb207', '2026-06-12 07:33:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b408a5d0-9f20-4e0d-b108-7ae1f7cbb208', 6029, 6032, 3900.0, 'EURO', 'TRANSFER', 'OPENED', 'b408a5d0-9f20-4e0d-b108-7ae1f7cbb208', '2026-06-12 07:36:00.000', NULL, NULL, 'transaction-service-transfer'),
('b409a5d0-9f20-4e0d-b108-7ae1f7cbb209', NULL, 6030, 16500.0, 'INR', 'DEPOSIT', 'OPENED', 'b409a5d0-9f20-4e0d-b108-7ae1f7cbb209', '2026-06-12 07:39:00.000', NULL, NULL, 'transaction-service-deposit'),
('b410a5d0-9f20-4e0d-b108-7ae1f7cbb210', 6031, NULL, 5000.0, 'USD', 'WITHDRAW', 'OPENED', 'b410a5d0-9f20-4e0d-b108-7ae1f7cbb210', '2026-06-12 07:42:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b411a5d0-9f20-4e0d-b108-7ae1f7cbb211', 6033, 6036, 14000.0, 'CHF', 'TRANSFER', 'OPENED', 'b411a5d0-9f20-4e0d-b108-7ae1f7cbb211', '2026-06-12 07:45:00.000', NULL, NULL, 'transaction-service-transfer'),
('b412a5d0-9f20-4e0d-b108-7ae1f7cbb212', NULL, 6034, 3100.0, 'EURO', 'DEPOSIT', 'OPENED', 'b412a5d0-9f20-4e0d-b108-7ae1f7cbb212', '2026-06-12 07:48:00.000', NULL, NULL, 'transaction-service-deposit'),
('b413a5d0-9f20-4e0d-b108-7ae1f7cbb213', 6035, NULL, 8500.0, 'INR', 'WITHDRAW', 'OPENED', 'b413a5d0-9f20-4e0d-b108-7ae1f7cbb213', '2026-06-12 07:51:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b414a5d0-9f20-4e0d-b108-7ae1f7cbb214', 6037, 6040, 9500.0, 'USD', 'TRANSFER', 'OPENED', 'b414a5d0-9f20-4e0d-b108-7ae1f7cbb214', '2026-06-12 07:54:00.000', NULL, NULL, 'transaction-service-transfer'),
('b415a5d0-9f20-4e0d-b108-7ae1f7cbb215', NULL, 6038, 1200.0, 'CHF', 'DEPOSIT', 'OPENED', 'b415a5d0-9f20-4e0d-b108-7ae1f7cbb215', '2026-06-12 07:57:00.000', NULL, NULL, 'transaction-service-deposit'),
('b416a5d0-9f20-4e0d-b108-7ae1f7cbb216', 6039, NULL, 22000.0, 'EURO', 'WITHDRAW', 'OPENED', 'b416a5d0-9f20-4e0d-b108-7ae1f7cbb216', '2026-06-12 08:00:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b417a5d0-9f20-4e0d-b108-7ae1f7cbb217', 6041, 6044, 7100.0, 'INR', 'TRANSFER', 'OPENED', 'b417a5d0-9f20-4e0d-b108-7ae1f7cbb217', '2026-06-12 08:03:00.000', NULL, NULL, 'transaction-service-transfer'),
('b418a5d0-9f20-4e0d-b108-7ae1f7cbb218', NULL, 6042, 13000.0, 'USD', 'DEPOSIT', 'OPENED', 'b418a5d0-9f20-4e0d-b108-7ae1f7cbb218', '2026-06-12 08:06:00.000', NULL, NULL, 'transaction-service-deposit'),
('b419a5d0-9f20-4e0d-b108-7ae1f7cbb219', 6043, NULL, 1800.0, 'CHF', 'WITHDRAW', 'OPENED', 'b419a5d0-9f20-4e0d-b108-7ae1f7cbb219', '2026-06-12 08:09:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b420a5d0-9f20-4e0d-b108-7ae1f7cbb220', 6045, 6048, 16000.0, 'EURO', 'TRANSFER', 'OPENED', 'b420a5d0-9f20-4e0d-b108-7ae1f7cbb220', '2026-06-12 08:12:00.000', NULL, NULL, 'transaction-service-transfer'),
('b421a5d0-9f20-4e0d-b108-7ae1f7cbb221', NULL, 6046, 7500.0, 'INR', 'DEPOSIT', 'OPENED', 'b421a5d0-9f20-4e0d-b108-7ae1f7cbb221', '2026-06-12 08:15:00.000', NULL, NULL, 'transaction-service-deposit'),
('b422a5d0-9f20-4e0d-b108-7ae1f7cbb222', 6047, NULL, 9000.0, 'USD', 'WITHDRAW', 'OPENED', 'b422a5d0-9f20-4e0d-b108-7ae1f7cbb222', '2026-06-12 08:18:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b423a5d0-9f20-4e0d-b108-7ae1f7cbb223', 6049, 6052, 3200.0, 'CHF', 'TRANSFER', 'OPENED', 'b423a5d0-9f20-4e0d-b108-7ae1f7cbb223', '2026-06-12 08:21:00.000', NULL, NULL, 'transaction-service-transfer'),
('b424a5d0-9f20-4e0d-b108-7ae1f7cbb224', NULL, 6050, 5000.0, 'EURO', 'DEPOSIT', 'OPENED', 'b424a5d0-9f20-4e0d-b108-7ae1f7cbb224', '2026-06-12 08:24:00.000', NULL, NULL, 'transaction-service-deposit'),
('b425a5d0-9f20-4e0d-b108-7ae1f7cbb225', 6051, NULL, 14000.0, 'INR', 'WITHDRAW', 'OPENED', 'b425a5d0-9f20-4e0d-b108-7ae1f7cbb225', '2026-06-12 08:27:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b426a5d0-9f20-4e0d-b108-7ae1f7cbb226', 6001, 6004, 5000.0, 'USD', 'TRANSFER', 'OPENED', 'b426a5d0-9f20-4e0d-b108-7ae1f7cbb226', '2026-06-12 08:30:00.000', NULL, NULL, 'transaction-service-transfer'),
('b427a5d0-9f20-4e0d-b108-7ae1f7cbb227', NULL, 6002, 12000.0, 'CHF', 'DEPOSIT', 'OPENED', 'b427a5d0-9f20-4e0d-b108-7ae1f7cbb227', '2026-06-12 08:33:00.000', NULL, NULL, 'transaction-service-deposit'),
('b428a5d0-9f20-4e0d-b108-7ae1f7cbb228', 6053, NULL, 25000.0, 'EURO', 'WITHDRAW', 'OPENED', 'b428a5d0-9f20-4e0d-b108-7ae1f7cbb228', '2026-06-12 08:36:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b429a5d0-9f20-4e0d-b108-7ae1f7cbb229', 6005, 6008, 1000.0, 'INR', 'TRANSFER', 'OPENED', 'b429a5d0-9f20-4e0d-b108-7ae1f7cbb229', '2026-06-12 08:39:00.000', NULL, NULL, 'transaction-service-transfer'),
('b430a5d0-9f20-4e0d-b108-7ae1f7cbb230', NULL, 6006, 4500.0, 'USD', 'DEPOSIT', 'OPENED', 'b430a5d0-9f20-4e0d-b108-7ae1f7cbb230', '2026-06-12 08:42:00.000', NULL, NULL, 'transaction-service-deposit'),
('b431a5d0-9f20-4e0d-b108-7ae1f7cbb231', 6007, NULL, 2000.0, 'CHF', 'WITHDRAW', 'OPENED', 'b431a5d0-9f20-4e0d-b108-7ae1f7cbb231', '2026-06-12 08:45:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b432a5d0-9f20-4e0d-b108-7ae1f7cbb232', 6009, 6013, 3000.0, 'EURO', 'TRANSFER', 'OPENED', 'b432a5d0-9f20-4e0d-b108-7ae1f7cbb232', '2026-06-12 08:48:00.000', NULL, NULL, 'transaction-service-transfer'),
('b433a5d0-9f20-4e0d-b108-7ae1f7cbb233', NULL, 6010, 18000.0, 'INR', 'DEPOSIT', 'OPENED', 'b433a5d0-9f20-4e0d-b108-7ae1f7cbb233', '2026-06-12 08:51:00.000', NULL, NULL, 'transaction-service-deposit'),
('b434a5d0-9f20-4e0d-b108-7ae1f7cbb234', 6011, NULL, 4000.0, 'USD', 'WITHDRAW', 'OPENED', 'b434a5d0-9f20-4e0d-b108-7ae1f7cbb234', '2026-06-12 08:54:00.000', NULL, NULL, 'transaction-service-withdraw'),
('b435a5d0-9f20-4e0d-b108-7ae1f7cbb235', 6012, 6015, 9000.0, 'CHF', 'TRANSFER', 'OPENED', 'b435a5d0-9f20-4e0d-b108-7ae1f7cbb235', '2026-06-12 08:57:00.000', NULL, NULL, 'transaction-service-transfer');