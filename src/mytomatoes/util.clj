(ns mytomatoes.util)



(defn result [r]
  {:status 200
   :body (str "{\"result\": \"" r "\"}")
   :headers {"Content-Type" "application/json"}})
