(ns mytomatoes.pages.login
  (:require [mytomatoes.layout :refer [with-layout]]
            [hiccup.core :refer [html]]))

(defn get-page [request]
  (with-layout request
    {:body
     (html
      [:div {:id "welcome"}
       [:p "mytomatoes.com helps you with the "
        [:a {:href "http://www.pomodorotechnique.com/"}
         "pomodoro technique"]
        " by "
        [:a {:href "http://francescocirillo.com/"}
         "Francesco Cirillo"]
        " - it's an online tomato kitchen timer and pomodoro tracker."]
       [:form {:action "/register"}
        [:a {:id "toggle_register_login" :href "#"}
         "already registered?"]
        [:h3 "register"]
        [:div {:id "fields"}
         [:input {:type "text" :id "username" :name "username"}]
         [:input {:type "password" :id "password" :name "password"}]
         [:input {:type "password" :id "password2" :name "password2"}]]
        [:input {:type "submit" :id "submit" :value "loading.." :disabled true}]
        [:div {:id "remember_me"}
         [:input {:type "checkbox" :id "remember" :name "remember" :checked true}]
         [:label {:for "remember"} " remember me"]]]])
     :script-bundles ["login.js"]}))
