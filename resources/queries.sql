-- name: account-by-username
-- Returns the account details for a given username
SELECT id, username, hashed_password, random_salt
       FROM Accounts WHERE username LIKE :username

-- name: insert-account<!
-- Creates a new account
INSERT INTO Accounts (username, hashed_password, random_salt, created_at)
       VALUES (:username, :hashed_password, :random_salt, CURRENT_TIMESTAMP)

-- name: tomatoes-by-account
-- Finds all tomatoes for an account
SELECT description, local_start, local_end
       FROM Tomatoes
       WHERE account_id = :account_id
       ORDER BY id DESC

-- name: account-preferences
-- Finds all preferences for an account
SELECT name, value
       FROM Preferences
       WHERE account_id = :account_id

-- name: set-preference!
-- Sets an account preference by a given to name to a value
INSERT INTO Preferences (account_id, name, value)
       VALUES (:account_id, :name, :value)
       ON DUPLICATE KEY UPDATE value = :value

-- name: account-by-remember-code
SELECT account_id FROM RememberCodes WHERE code = :code

-- name: remove-remember-code!
DELETE FROM RememberCodes WHERE code = :code

-- name: add-remember-code!
INSERT INTO RememberCodes (account_id, code)
       VALUES (:account_id, :code)
