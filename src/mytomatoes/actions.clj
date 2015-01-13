(ns mytomatoes.actions
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]))
(timbre/refer-timbre)

(defn result [r]
  {:status 200
   :body (str "{\"result\": \"" r "\"}")
   :headers {"Content-Type" "application/json"}})

(defn blank? [s]
  (or (not s)
      (not (seq s))
      (not (seq (str/trim s)))))

(defn account-exists [username]
  false)

(defn register [request]
  (let [username (get-in request [:params "username"])
        password (get-in request [:params "password"])
        password2 (get-in request [:params "password2"])
        remember (get-in request [:params "remember"])]
    (cond
      (blank? username)         (result "missing_username")
      (= username "username")   (result "missing_username")
      (blank? password)         (result "missing_password")
      (account-exists username) (result "unavailable_username")
      (not= password password2) (result "mismatched_passwords")
      :else

      (result "ok"))))
