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
            [quick-reset.core :refer [stop reset system go]]
            [taoensso.timbre :as log]))

(quick-reset.core/set-constructor 'mytomatoes.system/create-system)

(log/merge-config! {:appenders {:spit (log/spit-appender {:fname "debug.log"})}})
