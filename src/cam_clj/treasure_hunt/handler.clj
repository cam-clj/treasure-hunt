(ns cam-clj.treasure-hunt.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cam-clj.treasure-hunt.model :as m]
            [cam-clj.treasure-hunt.views.register :refer [registration-form
                                                          handle-registration]]
            [cam-clj.treasure-hunt.views.welcome :refer [welcome]]
            [cam-clj.treasure-hunt.views.leaderboard :refer [leaderboard]]
            [cam-clj.treasure-hunt.views.actions :refer [look move]]))

(defroutes app-routes
  (GET "/"
       []
       (registration-form))
  (GET "/leaderboard"
       []
       (leaderboard))
  (GET "/register"
       []
       (registration-form))
  (POST "/register"
        [team-name lang]
        (handle-registration team-name lang))
  (GET "/:team-id/welcome"
       [team-id]
       (welcome team-id))
  (GET "/:team-id/look"
       [team-id]
       (look team-id))
  (POST ["/:team-id/:direction" :direction #"north|east|south|west"]
        [team-id direction]
        (move team-id (keyword direction)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  []
  (m/init))

(defn wrap-log-request
  [handler]
  (fn [request]
    (let [response (handler request)]
      (log/info (:request-method request) (:uri request) (:params request) "-" (:status response))
      response)))

(def app
  (wrap-log-request (handler/site app-routes)))
