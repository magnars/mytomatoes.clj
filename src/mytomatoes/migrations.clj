(ns mytomatoes.migrations
  (:require [taoensso.timbre :as timbre]
            [yesql.core :refer [defqueries]]))
(timbre/refer-timbre)

(defqueries "migrations.sql")

(defmacro migration [db version num f]
  `(when (> ~num ~version)
     (info ~(str "Running migration " num ": " f))
     (~f ~db)))

(def latest 8)

(defn migrate! [db]
  (let [version (-> db get-version first :version)]

    (migration db version 6 drop-event-log!)
    (migration db version 7 add-account-id-index-to-tomatoes!)
    (migration db version 8 add-username-index-to-accounts!)

    (when (> latest version)
      (info "Updated system to newest version:" latest)
      (update-version! db latest))))
