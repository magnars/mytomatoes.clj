(ns mytomatoes.storage
  (:require [yesql.core :refer [defqueries]]))

(defqueries "queries.sql")

(defn account-exists? [db username]
  (seq (account-id-by-username db username)))
