(ns cam-clj.treasure-hunt.routes
  (:require [clojurewerkz.route-one.core :refer [defroute]]))

(defroute home "/")

(defroute register "/register")

(defroute welcome "/:team-id/welcome")

(defroute leaderboard "/leaderboard")

(defroute look "/:team-id/look")

(defroute move "/:team-id/:direction")
