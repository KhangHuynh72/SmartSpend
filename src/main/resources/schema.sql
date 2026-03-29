-- 1. Table for User Information
CREATE TABLE sec_user (
    userId            BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email             VARCHAR(100) NOT NULL UNIQUE,
    encryptedPassword VARCHAR(128) NOT NULL,
    enabled           BIT NOT NULL
);

-- 2. Table for Roles (USER, ADMIN, etc.)
CREATE TABLE sec_role (
    roleId   BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    roleName VARCHAR(30) NOT NULL UNIQUE
);

-- 3. Junction Table to link Users to Roles (Many-to-Many)
CREATE TABLE user_role (
    id     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    userId BIGINT NOT NULL,
    roleId BIGINT NOT NULL,
    FOREIGN KEY (userId) REFERENCES sec_user(userId),
    FOREIGN KEY (roleId) REFERENCES sec_role(roleId)
);

-- 4. Table for Transactions
CREATE TABLE transaction (
    id              BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    userId          BIGINT NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    transactionDate DATE NOT NULL,
    category        VARCHAR(100),
    description     VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES sec_user(userId)
);

-- 5. Table for Budgets
CREATE TABLE budget (
    id          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    userId      BIGINT NOT NULL,
    category    VARCHAR(100) NOT NULL,
    limitAmount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (userId) REFERENCES sec_user(userId),
    UNIQUE (userId, category)
);