-- name: account-id-by-username
-- Returns the account id for a given username
SELECT id FROM Accounts WHERE username LIKE :username
