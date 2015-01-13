(ns mytomatoes.storage
  (:require [mytomatoes.account :refer [get-random-salt hash-password]]
            [yesql.core :refer [defqueries]]))

(defqueries "queries.sql")

(defn account-exists? [db username]
  (seq (account-id-by-username db username)))

(defn create-account! [db username password]
  (let [salt (get-random-salt)]
    (-> (insert-account<! db username (hash-password password salt) salt)
        :generated_key)))
