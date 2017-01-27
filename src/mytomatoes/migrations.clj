(ns mytomatoes.migrations
  (:require [taoensso.timbre :as timbre]
            [yesql.core :refer [defqueries]]))
(timbre/refer-timbre)

(defqueries "migrations.sql")

(defmacro migration [db version num f]
  `(when (> ~num ~version)
     (info ~(str "Running migration " num ": " f))
     (~f {} ~db)))

(defn- delete-tomatoes-with-invalid-dates! [_ db]
  (delete-tomatoes-with-invalid-start-dates! {:date "0000-00-00 00:00:00"} db)
  (delete-tomatoes-with-invalid-end-dates! {:date "0000-00-00 00:00:00"} db))

(def latest 10)

(defn migrate! [db]
  (when (empty? (check-for-schema-info {} db))
    (create-table-schema-info! {} db)
    (set-initial-schema-version! {} db))
  (let [version (-> (get-version {} db) first :version)]
    (migration db version 1 create-table-accounts!)
    (migration db version 2 create-table-event-log!)
    (migration db version 3 create-table-remember-codes!)
    (migration db version 4 create-table-tomatoes!)
    (migration db version 5 create-table-preferences!)
    (migration db version 6 drop-event-log!)
    (migration db version 7 add-account-id-index-to-tomatoes!)
    (migration db version 8 add-username-index-to-accounts!)
    (migration db version 9 drop-tomatoes-updated-at-column!)
    (migration db version 10 delete-tomatoes-with-invalid-dates!)

    (when (> latest version)
      (info "Updated system to newest version:" latest)
      (update-version! {:version latest} db))))
