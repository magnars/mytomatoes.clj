(ns mytomatoes.actions
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [mytomatoes.storage :refer [account-exists?]]))
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

      (result "ok"))))
