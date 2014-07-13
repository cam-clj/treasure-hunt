(ns cam-clj.treasure-hunt.views.welcome
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [cam-clj.treasure-hunt.model :as m]
            [cam-clj.treasure-hunt.routes :as r]
            [cam-clj.treasure-hunt.views.common :refer [layout]]))

(defn- sample-session
  [team-id]
  (-> (slurp (io/resource "session.txt"))
      (str/replace "{{LOOK}}"  (r/look-url :team-id team-id))
      (str/replace "{{NORTH}}" (r/move-url :team-id team-id :direction "north"))
      (str/replace "{{EAST}}"  (r/move-url :team-id team-id :direction "east"))
      (str/replace "{{SOUTH}}" (r/move-url :team-id team-id :direction "south"))
      (str/replace "{{WEST}}"  (r/move-url :team-id team-id :direction "west"))))

(defn welcome
  [team-id]
  (when-let [team (m/get-team team-id)]
    (layout
     (html
      [:h1 "Welcome to the treasure hunt..."]
      [:blockquote
       "You are in a maze of twisty little passages, all alike."
       [:footer [:cite "Colossal Cave"]]]
      [:p "Your team has been assigned the id " [:code team-id] ". Make a note of this - you are going to need it to play."]
      [:p "Your objective is to write a program that will navigate the maze and find the treasure. Your program interacts with the server by issuing HTTP requests. The server understands the following commands:"]
      [:dl
       [:dt "LOOK - " [:code "GET " (r/look-url :team-id team-id)]]
       [:dd "Examine your current position"]
       [:dt "NORTH - " [:code "POST " (r/move-url :team-id team-id :direction "north")]]
       [:dd "Move north"]
       [:dt "EAST - " [:code "POST " (r/move-url :team-id team-id :direction "east")]]
       [:dd "Move east"]
       [:dt "SOUTH - " [:code "POST " (r/move-url :team-id team-id :direction "south")]]
       [:dd "Move south"]
       [:dt "WEST - " [:code "POST " (r/move-url :team-id team-id :direction "west")]]
       [:dd "Move west"]]
      [:p "Successful requests to the server will return HTTP status 200 (OK) and a plaintext body describing your current location or letting you know if you have found the treasure. Attempts to walk through a wall will return with HTTP status 400 (Bad Request), while unrecognized commands will receive HTTP status 404 (Not Found)."]
      [:p "An example session might look like:"]
      [:pre (sample-session team-id)]
      [:p "When you have found the treasure, check out the " (link-to {:target "_blank"} (r/leaderboard-url) "Leaderboard")
       " to see how your team fared."]))))
