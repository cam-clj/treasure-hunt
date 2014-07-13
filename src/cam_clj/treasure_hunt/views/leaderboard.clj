(ns cam-clj.treasure-hunt.views.leaderboard
  (:require [clj-time.format :as f]
            [cam-clj.treasure-hunt.model :as m]
            [cam-clj.treasure-hunt.util.interval :refer [seconds->str]]
            [cam-clj.treasure-hunt.views.common :refer [layout]]))

(defn render-date
  [d]
  (f/unparse (f/formatters :rfc822) d))

(def ^:private leaderboard-columns
  (partition 2 ["Team"            :team-name
                "Language"        :language
                "Started"         (comp render-date :started)
                "Time taken"      (comp seconds->str :time-taken)
                "Number of moves" :num-moves]))

(defn render-leaderboard-row
  [row]
  [:tr (map (fn [[_ accessor]]
              (vector :td (accessor row)))
            leaderboard-columns)])

(defn render-leaderboard-table
  [leaderboard]
  [:table.table.table-striped.table-bordered
   [:thead
    [:tr (map (fn [[label _]] (vector :th label)) leaderboard-columns)]]
   [:tbody
    (map render-leaderboard-row (sort-by :time-taken leaderboard))]])

(defn leaderboard
  []
  (let [leaderboard (m/leaderboard)]
    (layout
     [:div#leaderboard
      [:h1 "Leaderboard"]
      (if (not-empty leaderboard)
        (render-leaderboard-table leaderboard)
        [:p "No teams have found the treasure yet."])]
     :nav :leaderboard)))
