-- name: account-by-username
-- Returns the account details for a given username
SELECT id, username, hashed_password, random_salt
       FROM Accounts WHERE username LIKE :username

-- name: insert-account<!
-- Creates a new account
INSERT INTO Accounts (username, hashed_password, random_salt, created_at)
       VALUES (:username, :hashed_password, :random_salt, CURRENT_TIMESTAMP)

