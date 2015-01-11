(ns mytomatoes.server
  (:require [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as timbre]))
(timbre/refer-timbre)

(defn create-and-start
  [handler & {:keys [port]}]
  {:pre [(not (nil? port))]}
  (jetty/run-jetty handler {:port port :join? false})
  (info "Server started on port" port))

(defn stop
  [server]
  (.stop server))
