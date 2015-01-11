(ns mytomatoes.system
  (:require [mytomatoes.server :as server]
            [mytomatoes.web :as web]
            [prone.middleware :as prone]))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (let [sessions (atom {})
        handler (prone/wrap-exceptions (web/create-app sessions))
        server (server/create-and-start handler :port (:port system))]
    (assoc system
           :sessions sessions
           :handler handler
           :server server)))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (when (:server system)
    (server/stop (:server system)))
  (dissoc system :sessions :handler :server))

(defn create-system
  "Returns a new instance of the whole application."
  []
  {:port 3001
   :start start
   :stop stop})

(defn -main [& args]
  (let [system (create-system)]
    (start system)
    (println "Server started on port" (:port system))))
