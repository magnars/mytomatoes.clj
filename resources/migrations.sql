-- name: get-version
SELECT version FROM schema_info

-- name: update-version!
UPDATE schema_info SET version = :version

-- name: add-account-id-index-to-tomatoes!
CREATE INDEX tomatoes_account_index ON Tomatoes (account_id)

-- name: add-username-index-to-accounts!
CREATE INDEX accounts_username_index ON Accounts (username)

-- name: drop-event-log!
DROP TABLE EventLog;
