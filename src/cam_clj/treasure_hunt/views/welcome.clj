(ns cam-clj.treasure-hunt.views.welcome
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [cam-clj.treasure-hunt.model :as m]
            [cam-clj.treasure-hunt.views.common :refer [layout]]))

(defn- sample-session
  [team-id]
  (str/replace (slurp (io/resource "session.txt")) "{{TEAM_ID}" team-id))

(defn welcome
  [team-id]
  (when-let [team (m/get-team team-id)]
    (layout
     (html
      [:h1 "Welcome to the hunt..."]
      [:p "You are in a maze of twisty little passages, all alike. But can you find the treasure?"]
      [:p "Your team has been assigned the id " [:code team-id] ". Make a note of this - you are going to need it to play."]
      [:p "Your objective is to write a program that will navigate the maze in search of the treasure. Your program interacts with the server by issuing HTTP requests. The server understands the following commands:"]
      [:dl
       [:dt "LOOK - " [:code "GET /" team-id "/look"]]
       [:dd "Examine your current position"]
       [:dt "NORTH - " [:code "POST /" team-id "/north"]]
       [:dd "Move north"]
       [:dt "EAST - " [:code "POST /" team-id "/east"]]
       [:dd "Move east"]
       [:dt "SOUTH - " [:code "POST /" team-id "/south"]]
       [:dd "Move south"]
       [:dt "WEST - " [:code "POST /" team-id "/west"]]
       [:dd "Move west"]]
      [:p "Successful requests to the server will return HTTP status 200 (OK) and a plaintext body describing your current location or letting you know if you have found the treasure. Attempts to walk through a wall will return with HTTP status 400 (Bad Request), while unrecognized commands will receive HTTP status 404 (Not Found)."]
      [:p "An example session might look like:"]
      [:pre (sample-session team-id)]
      [:p "When you have found the treasure, check out the " (link-to {:target "_blank"} "/leaderboard" "Leaderboard")
       " to see how your team fared."])
     :nav :welcome)))