(ns mytomatoes.actions
  (:require [clojure.string :as str]
            [mytomatoes.storage :refer [account-exists? create-account! get-account]]
            [taoensso.timbre :as timbre]
            [mytomatoes.account :refer [password-matches?]]))
(timbre/refer-timbre)

(defn result [r]
  {:status 200
   :body (str "{\"result\": \"" r "\"}")
   :headers {"Content-Type" "application/json"}})

(defn blank? [s]
  (or (not s)
      (not (seq s))
      (not (seq (str/trim s)))))

(defn register [{:keys [db params]}]
  (let [username (get params "username")
        password (get params "password")
        password2 (get params "password2")
        remember (get params "remember")]
    (cond
      (blank? username)             (result "missing_username")
      (= username "username")       (result "missing_username")
      (blank? password)             (result "missing_password")
      (account-exists? db username) (result "unavailable_username")
      (not= password password2)     (result "mismatched_passwords")
      :else
      (let [account-id (create-account! db (str/trim username) password)]
        (info "Created account" username "with id" account-id)
        (result "ok")))))

(defn login [{:keys [db params]}]
  (let [username (get params "username")
        password (get params "password")
        remember (get params "remember")]
    (cond
      (blank? username) (result "missing_username")
      (= username "username") (result "missing_username")
      (blank? password) (result "missing_password")
      :else
      (let [account (get-account db (str/trim username))]
        (cond
          (nil? account) (result "unknown_username")
          (not (password-matches? account password))
          (do
            (info "Login attempt for" username "with id" (:id account) "with wrong password.")
            (result "wrong_password"))
          :else
          (do
            (info "Logged in" username "with id" (:id account) "using password.")
            (result "ok")))))))
