(ns mytomatoes.storage
  (:require [clj-time.coerce :refer [from-date to-local-date]]
            [clojure.string :as str]
            [mytomatoes.account :refer [get-random-salt hash-password]]
            [yesql.core :refer [defqueries]]))

(defqueries "queries.sql")

(defn account-exists? [db username]
  (seq (account-by-username {:username username} db)))

(defn create-account! [db username password]
  (let [salt (get-random-salt)]
    (-> (insert-account<! {:username username
                           :hashed_password (hash-password password salt)
                           :random_salt salt}
                          db)
        :generated_key)))

(defn change-password! [db account-id password]
  (let [salt (get-random-salt)]
    (update-password! {:hashed_password (hash-password password salt)
                       :random_salt salt
                       :account_id account-id}
                      db)))

(defn get-account [db username]
  (when-let [raw (first (account-by-username {:username username} db))]
    {:id (:id raw)
     :username (:username raw)
     :hashed-password (:hashed_password raw)
     :random-salt (:random_salt raw)}))

(defn get-tomatoes [db account-id]
  (map
   (fn [raw] {:local-start (from-date (:local_start raw))
              :local-end (from-date (:local_end raw))
              :date (to-local-date (:local_start raw))
              :description (:description raw)})
   (tomatoes-by-account {:account_id account-id} db)))

(defn ->keyword [s]
  (keyword (str/replace s "_" "-")))

(defn get-preferences [db account-id]
  (->> (account-preferences {:account_id account-id} db)
       (map (fn [p] [(->keyword (:name p))
                     (if (= "false" (:value p))
                       false (:value p))]))
       (into {})))

(defn get-account-id-by-remember-code [db code]
  (:account_id (first (account-by-remember-code {:code code} db))))
