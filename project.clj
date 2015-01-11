(defproject mytomatoes "0.1.0-SNAPSHOT"
  :description "The mytomatoes.com site, retro style"
  :url "http://mytomatoes.com"
  :jvm-opts ["-Xmx1G"
             "-XX:MaxPermSize=256m"
             "-Djava.awt.headless=true"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]
                 [ring "1.3.2" :exclusions [org.clojure/java.classpath]]
                 [hiccup "1.0.5"]
                 [optimus "0.15.1"]
                 [compojure "1.3.1"]
                 [pandect "0.4.1"]
                 [crypto-random "1.2.0"]
                 [commons-lang/commons-lang "2.6"]
                 [yesql "0.4.0"]
                 [mysql/mysql-connector-java "5.1.6"]]
  :main mytomatoes.web.system
  :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.8"]
                                  [ciderale/quick-reset "0.1.1"]
                                  [print-foo "1.0.1"]
                                  [prone "0.8.0"]]
                   :main user
                   :source-paths ["dev"]}})
