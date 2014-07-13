(ns cam-clj.treasure-hunt.model
  (:require [slingshot.slingshot :refer [throw+]]
            [clj-time.core :as t]
            [cam-clj.treasure-hunt.util.maze :as maze])
  (:import java.util.UUID))

(defonce world (atom nil))

(def ^:dynamic *maze-cols* 3)
(def ^:dynamic *maze-rows* 3)
(def ^:dynamic *loop-factor* nil)

(defn uuid [] (.toString (UUID/randomUUID)))

(defn init
  "Initialize a new world state."
  []
  (reset! world {:maze (maze/generate-maze *maze-cols* *maze-rows* *loop-factor*)
                 :treasure [(dec *maze-cols*) (dec *maze-rows*)]
                 :teams {}})
  nil)

(defn register-team!
  "Initialize and store a new team with the specified team-name.
   Return the generated id of the team."
  [team-name lang]
  (let [team-id (uuid)]
    (swap! world (fn [current-state]
                   (if (some #{team-name} (map :team-name (vals (:teams current-state))))
                     (throw+ {:error :duplicate-team-name})
                     (assoc-in current-state [:teams team-id]
                               {:team-id    team-id
                                :team-name  team-name
                                :language   lang
                                :position   [0 0]
                                :num-moves  0
                                :started    (t/now)}))))
    team-id))

(defn get-team
  [team-id]
  (get-in @world [:teams team-id]))

(defn- get-available-moves
  "Return the directions team-id can move from its current position."
  [current-state team-id]
  (if-let [position (get-in current-state [:teams team-id :position])]
    (maze/directions-from (:maze current-state) position)
    (throw+ {:error :invalid-team})))

(defn- update-position
  "Move team-id from its current position one step in the specified direction."
  [current-state team-id direction]
  (if-let [current-position (get-in current-state [:teams team-id :position])]
    (let [next-position (maze/apply-delta-direction direction current-position)]
      (if (contains? (maze/neighbours (:maze current-state) current-position) next-position)
        (-> current-state
            (assoc-in [:teams team-id :position] next-position)
            (update-in [:teams team-id :num-moves] inc))
        (throw+ {:error :invalid-direction})))
    (throw+ {:error :invalid-team})))

(defn look
  "Return the leagal moves team-id can move from its current position."
  [team-id]
  (get-available-moves @world team-id))

(defn move
  [team-id direction]
  (let [next-world (swap! world update-position team-id direction)
        team       (get-in next-world [:teams team-id])]
    (if (= (:treasure next-world) (:position team))
      (let [finished   (t/now) time-taken
            time-taken (t/in-seconds (t/interval (:started team) finished))]
        (swap! world update-in [:teams team-id] assoc :finished finished :time-taken time-taken)
        {:found-treasure? true
         :num-moves       (:num-moves team)
         :time-taken      time-taken})
      {:found-treasure? false
       :available-moves (get-available-moves next-world team-id)})))

(defn leaderboard
  []
  (map (juxt :team-name :language :started :finished :num-moves)
       (filter :finished (vals (:teams @world)))))
