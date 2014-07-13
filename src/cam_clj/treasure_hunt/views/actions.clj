(ns cam-clj.treasure-hunt.views.actions
  (:require [slingshot.slingshot :refer [try+]]
            [clojure.string :as str]
            [clj-time.core :as t]
            [ring.util.response :refer [response status content-type not-found]]
            [clj-http-status.constants :refer :all]
            [cam-clj.treasure-hunt.util.interval :refer [seconds->str]]
            [cam-clj.treasure-hunt.model :as m]))

(defn describe-exits
  [moves]
  (let [moves (map name moves)]
    (case (count moves)
      1 (format "There is an exit to the %s.\n"
                (first moves))
      2 (format "There are exits to the %s and %s.\n"
                (first moves) (second moves))
      (format "There are exits to the %s and %s.\n"
              (str/join ", " (butlast moves))
              (last moves)))))

(defn describe
  [moves]
  (str "You are in a maze of twisty little passages, all alike.\n"
       (describe-exits moves)))

(defn look-response
  [available-moves]
  (-> (response (describe available-moves))
      (content-type "text/plain")))

(defn found-treasure-response
  [{:keys [time-taken num-moves]}]
  (-> (response (str "Congratulations, you found the treasure!\n"
                     "You made " num-moves " moves and took " (seconds->str time-taken) " to complete the puzzle.\n"))
      (content-type "text/plain")))

(defn look
  [team-id]
  (try+
   (let [available-moves (m/look team-id)]
     (look-response available-moves))
   (catch [:error :invalid-team] _
       (not-found (str "Unrecognized team " team-id)))))

(defn move
  [team-id direction]
  (try+
   (let [res (m/move team-id direction)]
     (if (:found-treasure? res)
       (found-treasure-response res)
       (look-response (:available-moves res))))
   (catch [:error :invalid-team] _
       (not-found (str "Unrecognized team " team-id)))
   (catch [:error :invalid-direction] _
       (-> (response "You can't walk through walls!\n")
           (status HTTP_BAD_REQUEST)))))
