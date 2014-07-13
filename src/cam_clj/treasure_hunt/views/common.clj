(ns cam-clj.treasure-hunt.views.common
  (:require [hiccup.core :as h]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [html5 include-css]]))

(defn layout
  [content & {:keys [title css]
              :or {title "Cambridge NonDysfunctional Programmers Treasure Hunt"}}]
  (html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "icon" :type "images/x-icon" :href "/favicon.ico"}]
    [:link {:rel "shortcut icon" :type "images/x-icon" :href "/favicon.ico"}]
    [:title title]
    (include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"
                 "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css")
    (when css
      (apply include-css css))]
   [:body
    [:div.container content]]))
