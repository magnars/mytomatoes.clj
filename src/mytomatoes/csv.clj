(ns mytomatoes.csv
  (:require [clojure-csv.core :refer [write-csv]]
            [clj-time.format :refer [formatters unparse]]
            [mytomatoes.storage :refer [get-tomatoes]]))

(def formatter (formatters :date-hour-minute-second))

(defn- tomato-fields [tomato]
  [(unparse formatter (:local-start tomato))
   (unparse formatter (:local-end tomato))
   (:description tomato)])

(defn render-tomatoes [{:keys [db session]}]
  {:body (let [tomatoes (get-tomatoes db (:account-id session))]
           (write-csv (map tomato-fields tomatoes) :force-quote true))
   :status 200
   :headers {"Content-Type" "text/csv; charset=utf-8"
             "Content-Disposition" "attachment; filename=mytomatoes.csv"}})
