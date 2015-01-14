(ns mytomatoes.storage
  (:require [mytomatoes.account :refer [get-random-salt hash-password]]
            [yesql.core :refer [defqueries]]))

(defqueries "queries.sql")

(defn account-exists? [db username]
  (seq (account-by-username db username)))

(defn create-account! [db username password]
  (let [salt (get-random-salt)]
    (-> (insert-account<! db username (hash-password password salt) salt)
        :generated_key)))

(defn get-account [db username]
  (when-let [raw (first (account-by-username db username))]
    {:id (:id raw)
     :username (:username raw)
     :hashed-password (:hashed_password raw)
     :random-salt (:random_salt raw)}))

(defn get-tomatoes [db account-id]
  (tomatoes-by-account db account-id))
