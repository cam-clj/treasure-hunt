(defproject cam-clj/treasure-hunt "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.0"]
                 [log4j/log4j "1.2.17"]
                 [slingshot "0.10.3"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [clj-time "0.7.0"]
                 [clj-http-status "0.1.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler cam-clj.treasure-hunt.handler/app
         :nrepl   {:start? true}
         :init    cam-clj.treasure-hunt.handler/init}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [org.clojure/tools.trace "0.7.8"]]}})
