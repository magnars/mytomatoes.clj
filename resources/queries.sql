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
