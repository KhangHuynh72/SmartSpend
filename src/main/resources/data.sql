-- The password for both is '1234'
-- {bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qnuy6SeqBYpsquymaB67pS4K4.p661Scca

INSERT INTO sec_user (email, encryptedPassword, enabled) VALUES 
('frank@frank.com', '$2a$10$ViYIjR7TPoaZUJscgXx33e6niPFKDpBQg6G4zpIacOCVoE6XRFyBe', 1),
('simon@humber.ca', '$2a$10$ViYIjR7TPoaZUJscgXx33e6niPFKDpBQg6G4zpIacOCVoE6XRFyBe', 1);

INSERT INTO sec_role (roleName) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Link Frank to USER, Simon to ADMIN
INSERT INTO user_role (userId, roleId) VALUES (1, 1), (2, 2);