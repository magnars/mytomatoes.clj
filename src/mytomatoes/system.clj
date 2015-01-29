(ns mytomatoes.system
  (:gen-class :main true)
  (:require [clojure.java.io :as io]
            [clojure.tools.nrepl.server :as nrepl]
            [com.postspectacular.rotor :as rotor]
            [mytomatoes.migrations :refer [migrate!]]
            [mytomatoes.server :as server]
            [mytomatoes.web :as web]
            [taoensso.timbre.appenders.3rd-party.rolling :as rolling-appender]
            [taoensso.timbre.appenders.postal :as postal-appender]
            [taoensso.timbre :as log]))

(defn send-error-emails [{:keys [host port user pass from to]}]
  (log/merge-config!
   {:appenders {:postal
                (postal-appender/postal-appender
                 ^{:host host :port port :user user :pass pass}
                 {:from from :to to})}})
  (log/info "Sending errors to" to "via" host))

(defn write-logs-to-file [conf]
  (log/merge-config!
   {:appenders {:rolling
                (rolling-appender/rolling-appender conf)}})
  (log/info "Writing logs to" (pr-str (:path conf)) (name (:pattern conf :daily))))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (migrate! {:connection (:db system)})
  (let [handler (web/create-app {:connection (:db system)}
                                (:memcached system)
                                (:env system))
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
      (write-logs-to-file {:path (:rotor-log-file system)
                           :pattern :weekly}))

    (when (:mail system)
      (send-error-emails (:mail system)))

    (let [repl (nrepl/start-server :port (:repl-port system 0) :bind "127.0.0.1")]
      (log/info "Repl started at" (:port repl)))))
