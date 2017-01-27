-- name: check-for-schema-info
SHOW TABLES LIKE 'schema_info'

-- name: create-table-schema-info!
CREATE TABLE schema_info (version INT, PRIMARY KEY (version))

-- name: set-initial-schema-version!
INSERT INTO schema_info VALUES (0)

-- name: create-table-accounts!
CREATE TABLE Accounts (
id                INT UNSIGNED         NOT NULL AUTO_INCREMENT,
username          VARCHAR(255)         NOT NULL,
hashed_password   VARCHAR(255)         NOT NULL,
random_salt       VARCHAR(255)         NOT NULL,
updated_at        TIMESTAMP,
created_at        TIMESTAMP,
PRIMARY KEY (id))

-- name: create-table-event-log!
CREATE TABLE EventLog (
id                INT UNSIGNED                 NOT NULL AUTO_INCREMENT,
event             VARCHAR(255)                 NOT NULL,
ip_address        VARCHAR(15)                  NOT NULL,
account_id        INT UNSIGNED,
details           VARCHAR(255),
time              TIMESTAMP,
PRIMARY KEY (id))

-- name: create-table-remember-codes!
CREATE TABLE RememberCodes (
code              VARCHAR(64)                   NOT NULL,
account_id        INT UNSIGNED                  NOT NULL,
created_at        TIMESTAMP                     NOT NULL,
PRIMARY KEY (code))

-- name: create-table-tomatoes!
CREATE TABLE Tomatoes (
id                INT UNSIGNED                 NOT NULL AUTO_INCREMENT,
account_id        INT UNSIGNED                 NOT NULL,
status            ENUM ('started', 'completed', 'squashed') NOT NULL DEFAULT 'started',
description       VARCHAR(255),
local_start       TIMESTAMP,
local_end         TIMESTAMP,
created_at        TIMESTAMP,
updated_at        TIMESTAMP,
PRIMARY KEY (id))

-- name: create-table-preferences!
CREATE TABLE Preferences (
account_id        MEDIUMINT UNSIGNED           NOT NULL,
name              VARCHAR(255)                 NOT NULL,
value             VARCHAR(255),
PRIMARY KEY (account_id, name))

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

-- name: drop-tomatoes-updated-at-column!
ALTER TABLE Tomatoes DROP COLUMN updated_at;

-- name: delete-tomatoes-with-invalid-start-dates!
DELETE FROM Tomatoes WHERE local_start = :date

-- name: delete-tomatoes-with-invalid-end-dates!
DELETE FROM Tomatoes WHERE local_end = :date
