(ns mytomatoes.actions
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [mytomatoes.account :refer [password-matches?]]
            [mytomatoes.login :refer [remember! generate-auth-token]]
            [mytomatoes.storage :as s]
            [mytomatoes.util :refer [result]]
            [mytomatoes.word-stats :as ws]
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

(defn keep-session-alive []
  (result "ok"))

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
    (info "Set preference" name "=" value "for" account-id)
    (result "ok")))

(defn complete-tomato [{:keys [db params session]}]
  (let [account-id (:account-id session)
        start-time (get params "start_time")
        end-time (get params "end_time")
        description (get params "description")]
    (s/insert-complete-tomato! db account-id description start-time end-time)
    (info "Complete tomato for" account-id ":" description)
    (result "ok")))

(defn check-my-words [{:keys [db params]}]
  (let [username (get params "username")
        word1 (get params "word1")
        word2 (get params "word2")
        word3 (get params "word3")
        word4 (get params "word4")
        proposed-words (into #{} (map str/lower-case [word1 word2 word3 word4]))]
    (cond
      (blank? username) (result "missing_username")
      (blank? word1) (result "missing_word1")
      (blank? word2) (result "missing_word2")
      (blank? word3) (result "missing_word3")
      (blank? word4) (result "missing_word4")
      (> 4 (count proposed-words)) (result "duplicate_words")
      (> 4 (count (set/difference proposed-words ws/common-words))) (do
                                                                      (warn "Attempt at using common words in recovery, " username ":" (set/intersection proposed-words ws/common-words))
                                                                      (result "too_common_words"))
      :else
      (let [account (s/get-account db (str/trim username))]
        (if (nil? account)
          (result "unknown_username")
          (let [actual-words (ws/words-for-account db (:id account))
                matches (count (set/intersection
                                actual-words
                                proposed-words))]
            (cond
              (= 4 matches) (let [code (generate-auth-token)]
                              (info "Successfull password word check for" username "with id" (:id account) ":" proposed-words)
                              (s/add-remember-code! db (:id account) code)
                              (result "ok" {:url (str "/change-password?code=" code)}))
              (= 0 matches) (do
                              (info "Failed password word check with NO matching words for" username "with id" (:id account) ":" proposed-words)
                              (result "no_matches"))
              :else (do
                      (info "Failed password word check with" matches "out of 4 matches for" username "with id" (:id account) ", wrong:" (set/difference proposed-words actual-words))
                      (result "not_enough_matches")))))))))

(defn change-password [{:keys [db params]}]
  (let [code (get params "code")
        password (get params "password")
        password2 (get params "password2")]
    (cond
      (blank? code) (do (info "Attempt to change password without a code.")
                        (result "wrong_code"))
      (blank? password) (result "missing_password")
      (not= password password2) (result "mismatched_passwords")
      :else
      (if-let [account-id (s/get-account-id-by-remember-code db code)]
        (do
          (s/change-password! db account-id password)
          (s/remove-remember-code! db code)
          (info "Password changed for account" account-id)
          (-> (result "ok")
              (assoc :session {:account-id account-id})
              (remember! db account-id)))
        (do
          (info "Attempt to change password with invalid code: " code)
          (result "wrong_code"))))))
