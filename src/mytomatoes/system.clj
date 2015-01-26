(ns mytomatoes.system
  (:gen-class :main true)
  (:require [clojure.java.io :as io]
            [mytomatoes.server :as server]
            [mytomatoes.web :as web]
            [mytomatoes.migrations :refer [migrate!]]
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.timbre :as timbre :refer [info]]
            [com.postspectacular.rotor :as rotor]))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (migrate! (:db system))
  (let [handler (web/create-app (:db system) (:memcached system) (:env system))
        server (server/create-and-start handler :port (:port system))]
    (assoc system
           :handler handler
           :server server)))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (when (:server system)
    (server/stop (:server system)))
  (dissoc system :handler :server))

(defn create-system
  "Returns a new instance of the whole application."
  []
  (merge (read-string (slurp (io/resource "mytomatoes-config.edn")))
         {:start start
          :stop stop}))

(defn -main [& args]
  (let [system (create-system)]
    (start system)

    (when (:rotor-log-file system)
      (timbre/set-config! [:appenders :rotor]
                          {:min-level :info
                           :enabled? true
                           :async? false                    ; should be always false for rotor
                           :max-message-per-msecs nil
                           :fn rotor/append})
      (timbre/set-config! [:shared-appender-config :rotor]
                          {:path (:rotor-log-file system) :max-size (* 512 1024) :backlog 10})
      (info "Rotor log file at" (:rotor-log-file system)))

    (let [repl (nrepl/start-server :port (:repl-port system 0) :bind "127.0.0.1")]
      (info "Repl started at" (:port repl)))))
