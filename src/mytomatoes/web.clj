(ns mytomatoes.web)

(defn create-app [sessions]
  (fn [req]
    {:status 200
     :body "Hello world"}))
