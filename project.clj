(defproject mytomatoes "0.1.0-SNAPSHOT"
  :description "The mytomatoes.com site, retro style"
  :url "http://mytomatoes.com"
  :jvm-opts ["-Xmx1G"
             "-Djava.awt.headless=true"
             "-Dfile.encoding=UTF-8"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-time "0.11.0"]
                 [ring "1.4.0" :exclusions [org.clojure/java.classpath]]
                 [hiccup "1.0.5"]
                 [optimus "0.18.3"]
                 [compojure "1.4.0"]
                 [pandect "0.5.4"]
                 [crypto-random "1.2.0"]
                 [commons-lang/commons-lang "2.6"]
                 [yesql "0.5.1"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [com.taoensso/timbre "4.1.4"]
                 [inflections "0.10.0"]
                 [clojure-csv "2.0.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [com.postspectacular/rotor "0.1.0"]
                 [cheshire "5.5.0"]
                 [ring-session-memcached "0.0.1"]
                 [org.slf4j/slf4j-nop "1.7.7"]
                 [com.draines/postal "1.11.4"]]
  :main mytomatoes.system
  :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.9"]
                                  [ciderale/quick-reset "0.2.0"]
                                  [print-foo "1.0.2"]
                                  [prone "0.8.2"]]
                   :main user
                   :source-paths ["dev"]}
             :uberjar {:aot :all}})
