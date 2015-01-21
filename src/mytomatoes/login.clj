(ns mytomatoes.login
  (:require [crypto.random]
            [mytomatoes.util :refer [result]]
            [mytomatoes.storage :refer [get-account-id-by-remember-code]]
            [mytomatoes.storage :refer [remove-remember-code!]]
            [mytomatoes.storage :refer [add-remember-code!]]
            [taoensso.timbre :refer [info]]
            [ring.util.response :refer [redirect]])
  (:import [org.apache.commons.codec.binary Hex]))

(defn json-request? [req]
  (re-find #"application/json" (get-in req [:headers "accept"])))

(defn redirect-if-not-logged-in [handler]
  (fn [req]
    (if (:account-id (:session req))
      (handler req)
      (if (json-request? req)
        (result "not_logged_in")
        (redirect "/?session=expired")))))

(defn redirect-if-logged-in [handler]
  (fn [req]
    (if (:account-id (:session req))
      (if (json-request? req)
        (result "already_logged_in")
        (redirect "/"))
      (handler req))))

(defn generate-auth-token
  []
  (-> (crypto.random/bytes 32)
      (Hex/encodeHex)
      (String.)))

(defn log-in-with-remember-code [db remember-code]
  (when-let [account-id (get-account-id-by-remember-code db remember-code)]
    (remove-remember-code! db remember-code)
    account-id))

(defn remember! [response db account-id]
  (let [code (generate-auth-token)]
    (add-remember-code! db account-id code)
    (assoc-in response [:cookies "mytomatoes_remember"] {:value code
                                                         :path "/"})))

(defn wrap-remember-code [handler]
  (fn [req]
    (if-not (:account-id (:session req))
      (if-let [remember-code (get-in req [:cookies "mytomatoes_remember" :value])]
        (if-let [account-id (log-in-with-remember-code (:db req) remember-code)]
          (do
            (info "Logged in account with id" account-id "using remember code.")
            (-> (handler (assoc-in req [:session :account-id] account-id))
                (assoc-in [:session :account-id] account-id)
                (remember! (:db req) account-id)))
          (handler req))
        (handler req))
      (handler req))))
