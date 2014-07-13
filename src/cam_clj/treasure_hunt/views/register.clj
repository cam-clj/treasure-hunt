(ns cam-clj.treasure-hunt.views.register
  (:require [clojure.string :as str]
            [slingshot.slingshot :refer [try+]]
            [ring.util.response :refer [redirect]]
            [hiccup.form :refer [form-to text-field]]
            [cam-clj.treasure-hunt.views.common :refer [layout]]
            [cam-clj.treasure-hunt.routes :as r]
            [cam-clj.treasure-hunt.model :as m]))

(defn registration-form
  [& {:keys [error prefill]}]
  (layout
   (form-to
    {:class "form-signin" :role "form"} [:post (r/register-url)]
    [:h2.form-signin-heading "Register your team"]
    (let [form-class (if error "form-group has-error" "form-group")]
      [:div {:class form-class}
       (text-field {:class "form-control" :placeholder "Team Name" :required true :autofocus true}
                   "team-name")
       (when error
         [:span.help-block error])])
    [:div.form-group
     (text-field {:class "form-control" :placeholder "Programming Language" :required true}
                 "lang" (:lang prefill))]
    [:button.btn.btn-lg.btn-primary.btn-block {:type "submit"} "Get started"])
   :css ["/css/signin.css"]))

(defn handle-registration
  [team-name lang]
  (if (str/blank? team-name)
    (registration-form :error "Please enter your team name")
    (try+
     (let [team-id (m/register-team! team-name lang)]
       (redirect (r/welcome-url :team-id team-id)))
     (catch [:error :duplicate-team-name] _
       (registration-form :error (str "Sorry, " team-name " is already taken")
                          :prefill {:lang lang})))))
