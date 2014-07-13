(ns cam-clj.treasure-hunt.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojurewerkz.route-one.core :refer [with-base-url]]
            [cam-clj.treasure-hunt.model :as m]
            [cam-clj.treasure-hunt.routes :as r]
            [cam-clj.treasure-hunt.views.register :refer [registration-form
                                                          handle-registration]]
            [cam-clj.treasure-hunt.views.welcome :refer [welcome]]
            [cam-clj.treasure-hunt.views.leaderboard :refer [leaderboard]]
            [cam-clj.treasure-hunt.views.actions :refer [look move]])
  (:import java.net.URL))

(defroutes app-routes
  (GET r/home-template
       []
       (registration-form))
  (GET r/leaderboard-template
       []
       (leaderboard))
  (GET r/register-template
       []
       (registration-form))
  (POST r/register-template
        [team-name lang]
        (handle-registration team-name lang))
  (GET r/welcome-template
       [team-id]
       (welcome team-id))
  (GET r/look-template
       [team-id]
       (look team-id))
  (POST [r/move-template :direction #"north|east|south|west"]
        [team-id direction]
        (move team-id (keyword direction)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  []
  (let [start (System/currentTimeMillis)]
    (log/info "Initializing system")
    (m/init)
    (log/info "System initialization completed in" (int (/ (- (System/currentTimeMillis) start) 1000)) "seconds")))

(defn wrap-log-request
  [handler]
  (fn [request]
    (let [response (handler request)]
      (log/info (:request-method request) (:uri request) (:params request) "-" (:status response))
      response)))

(defn wrap-base-url
  [handler]
  (fn [request]
    (let [scheme   (get-in request [:headers "x-forwarded-scheme"] (name (:scheme request)))
          host     (:server-name request)
          port     (:server-port request)
          path     (:context request "")
          base-url (if (or (= port 80) (= port 443))
                     (URL. scheme host path)
                     (URL. scheme host port path))]
      (with-base-url base-url (handler request)))))

(def app
  (-> (handler/site app-routes)
      (wrap-base-url)
      (wrap-log-request)))
