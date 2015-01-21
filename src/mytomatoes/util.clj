(ns mytomatoes.util
  (:require [cheshire.core :refer [generate-string]]))

(defn result [r & [more]]
  {:status 200
   :body (generate-string (merge {:result r} more))
   :headers {"Content-Type" "application/json"}})
