(ns mytomatoes.actions
  (:require [clojure.string :as str]
            [mytomatoes.account :refer [password-matches?]]
            [mytomatoes.login :refer [remember!]]
            [mytomatoes.storage :as s]
            [mytomatoes.util :refer [result]]
            [taoensso.timbre :as timbre]))
(timbre/refer-timbre)

(defn blank? [s]
  (or (not s)
      (not (seq s))
      (not (seq (str/trim s)))))

(defn register [{:keys [db params]}]
  (let [username (get params "username")
        password (get params "password")
        password2 (get params "password2")
        remember? (get params "remember")]
    (cond
      (blank? username)             (result "missing_username")
      (= username "username")       (result "missing_username")
      (blank? password)             (result "missing_password")
      (s/account-exists? db username) (result "unavailable_username")
      (not= password password2)     (result "mismatched_passwords")
      :else
      (let [account-id (s/create-account! db (str/trim username) password)]
        (info "Created account" username "with id" account-id)
        (-> (result "ok")
            (assoc :session {:account-id account-id})
            (cond-> remember? (remember! db account-id)))))))

(defn login [{:keys [db params]}]
  (let [username (get params "username")
        password (get params "password")
        remember? (get params "remember")]
    (cond
      (blank? username) (result "missing_username")
      (= username "username") (result "missing_username")
      (blank? password) (result "missing_password")
      :else
      (let [account (s/get-account db (str/trim username))]
        (cond
          (nil? account) (result "unknown_username")
          (not (password-matches? account password))
          (do
            (info "Login attempt for" username "with id" (:id account) "with wrong password.")
            (result "wrong_password"))
          :else
          (do
            (info "Logged in" username "with id" (:id account) "using password.")
            (-> (result "ok")
                (assoc :session {:account-id (:id account)})
                (cond-> remember? (remember! db (:id account))))))))))

(defn logout []
  {:session {}
   :cookies {"mytomatoes_remember" {:value "" :path "/"}}
   :status 302
   :headers {"Location" "/"}})

(defn set-preference [{:keys [db params session]}]
  (let [name (get params "name")
        value (get params "value" "y")
        account-id (:account-id session)]
    (s/set-preference! db account-id name value)
    (result "ok")))
