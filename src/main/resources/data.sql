-- The password for both is '1234'
-- {bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qnuy6SeqBYpsquymaB67pS4K4.p661Scca

INSERT INTO sec_user (email, encryptedPassword, enabled) VALUES 
('frank@frank.com', '$2a$10$ViYIjR7TPoaZUJscgXx33e6niPFKDpBQg6G4zpIacOCVoE6XRFyBe', 1),
('simon@humber.ca', '$2a$10$ViYIjR7TPoaZUJscgXx33e6niPFKDpBQg6G4zpIacOCVoE6XRFyBe', 1);

INSERT INTO sec_role (roleName) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Link Frank to USER, Simon to ADMIN
INSERT INTO user_role (userId, roleId) VALUES (1, 1), (2, 2);

-- Add some dummy transactions for Frank (userId 1)
INSERT INTO transaction (userId, amount, transactionDate, category, description) VALUES
(1, -50.25, '2026-03-20', 'Food', 'Grocery Store'),
(1, -15.99, '2026-03-21', 'Subscriptions', 'Netflix'),
(1, -120.00, '2026-03-22', 'Utilities', 'Hydro Bill'),
(1, 1500.00, '2026-03-25', 'Income', 'Paycheck'),
(1, -25.50, '2026-03-26', 'Entertainment', 'Movie Tickets');

-- Add some dummy budgets for Frank (userId 1)
INSERT INTO budget (userId, category, limitAmount) VALUES
(1, 'Food', 300.00),
(1, 'Entertainment', 100.00),
(1, 'Utilities', 150.00);