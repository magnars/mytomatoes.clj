(ns mytomatoes.storage
  (:require [clj-time.coerce :refer [from-date to-local-date]]
            [clojure.string :as str]
            [mytomatoes.account :refer [get-random-salt hash-password]]
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
  (map
   (fn [raw] {:local-start (from-date (:local_start raw))
              :local-end (from-date (:local_end raw))
              :date (to-local-date (:local_start raw))
              :description (:description raw)})
   (tomatoes-by-account db account-id)))

(defn ->keyword [s]
  (keyword (str/replace s "_" "-")))

(defn get-preferences [db account-id]
  (->> (account-preferences db account-id)
       (map (fn [p] [(->keyword (:name p))
                     (if (= "false" (:value p))
                       false (:value p))]))
       (into {})))

(defn get-account-id-by-remember-code [db code]
  (:account_id (first (account-by-remember-code db code))))
