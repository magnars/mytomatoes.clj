-- name: account-id-by-username
-- Returns the account id for a given username
SELECT id FROM Accounts WHERE username LIKE :username

-- name: insert-account<!
-- Creates a new account
INSERT INTO Accounts (username, hashed_password, random_salt, created_at)
     VALUES (:username, :hashed_password, :random_salt, CURRENT_TIMESTAMP)

