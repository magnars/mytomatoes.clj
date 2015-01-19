(ns mytomatoes.system
  (:gen-class :main true)
  (:require [clojure.java.io :as io]
            [mytomatoes.server :as server]
            [mytomatoes.web :as web]
            [mytomatoes.migrations :refer [migrate!]]
            [clojure.tools.nrepl.server :as nrepl]))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (migrate! (:db system))
  (let [sessions (atom {})
        handler (web/create-app (:db system) sessions (:env system))
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
  (merge (read-string (slurp (io/resource "mytomatoes-config.edn")))
         {:start start
          :stop stop}))

(defn -main [& args]
  (let [system (create-system)]
    (start system)
    (let [repl (nrepl/start-server :port (:repl-port system 0) :bind "127.0.0.1")]
      (println "Repl started at" (:port repl)))))
