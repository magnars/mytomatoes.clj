(ns user
  (:require [clj-time.core :as time]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint pp]]
            [clojure.repl :refer :all]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.tools.trace :refer [trace-ns]]
            [mytomatoes.system]
            [mytomatoes.storage :as st]
            [print.foo :refer :all]
            [quick-reset.core :refer [stop reset system]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)
(quick-reset.core/set-constructor 'mytomatoes.system/create-system)

(timbre/set-config! [:appenders :spit :enabled?] true)
(timbre/set-config! [:shared-appender-config :spit-filename] "debug.log")
