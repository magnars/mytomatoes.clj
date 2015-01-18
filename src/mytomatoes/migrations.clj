(ns mytomatoes.migrations
  (:require [taoensso.timbre :as timbre]
            [yesql.core :refer [defqueries]]))
(timbre/refer-timbre)

(defqueries "migrations.sql")

(defmacro migration [db version num f]
  `(when (> ~num ~version)
     (info ~(str "Running migration " num ": " f))
     (~f ~db)))

(defn- delete-tomatoes-with-invalid-dates! [db]
  (delete-tomatoes-with-invalid-start-dates! db "0000-00-00 00:00:00")
  (delete-tomatoes-with-invalid-end-dates! db "0000-00-00 00:00:00"))

(def latest 10)

(defn migrate! [db]
  (let [version (-> db get-version first :version)]

    (migration db version 6 drop-event-log!)
    (migration db version 7 add-account-id-index-to-tomatoes!)
    (migration db version 8 add-username-index-to-accounts!)
    (migration db version 9 drop-tomatoes-updated-at-column!)
    (migration db version 10 delete-tomatoes-with-invalid-dates!)

    (when (> latest version)
      (info "Updated system to newest version:" latest)
      (update-version! db latest))))
