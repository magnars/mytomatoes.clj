(ns mytomatoes.migrations
  (:require [taoensso.timbre :as timbre]
            [yesql.core :refer [defqueries]]))
(timbre/refer-timbre)

(defqueries "migrations.sql")

(def latest 7)

(defn migrate! [db]
  (let [version (-> db get-version first :version)]
    (when (> 6 version)
      (info "Running migration 6: add-account-id-index-to-tomatoes!")
      (add-account-id-index-to-tomatoes! db))
    (when (> 7 version)
      (info "Running migration 7: add-username-index-to-accounts!")
      (add-username-index-to-accounts! db))
    (when (> latest version)
      (info "Updated system to newest version:" latest)
      (update-version! db latest))))
