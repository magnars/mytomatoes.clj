(ns mytomatoes.pages.homepage
  (:require [mytomatoes.layout :refer [with-layout]]
            [hiccup.core :refer [html]]))

(defn- render-states []
  (html
   [:ul {:id "states"}
    [:li {:id "waiting"}
     [:div {:id "flash_message"} "&nbsp;"]
     [:a {:href "#"} "start tomato"]]
    [:li {:id "working"}
     [:div {:id "time_left"} "25 min "]
     [:div {:id "cancel"} [:a {:href "#"} "squash tomato"]]]
    [:li {:id "stop_working"}
     [:div {:id "no_time_left"} "0:00"]
     [:div {:id "break"} "time for a break"]]
    [:li {:id "enter_description"}
     [:div {:id "congratulations"} "congrats!" [:span "first"] " finished tomato today"]
     [:div {:id "description"} "what did you do?"]
     [:form [:input {:type "text" :maxlength "255"}]]
     [:div {:id "void"} "or " [:a {:href "#"} "squash tomato"]]]
    [:li {:id "on_a_break"}
     [:div {:id "break_left"} "5 min"]
     [:div {:id "well_deserved"} "a well deserved break"]
     [:div {:id "longer_break" :class "longer_break_closed"}
      [:a {:id "toggle_longer_break" :href "#"} "take a longer break"]
      [:span ": "
       [:a {:href "#"} "10"]
       [:a {:href "#"} "15"]
       [:a {:href "#"} "20"]
       [:a {:href "#"} "25"] " min"]]]
    [:li {:id "break_over"}
     [:div {:id "no_break_left"} "0:00"]
     [:div {:id "back_to_work"} "back to work!"]]]))

(defn- render-preferences [])

(defn- render-tutorial [])

(defn- render-completed-tomatoes [request])

(defn- render-audio [])

(defn- render-scripts [request])

(defn get-page [request]
  (with-layout request
    {:body
     (str (render-states)
          (render-preferences)
          (render-tutorial)
          (render-completed-tomatoes request)
          (render-audio)
          (render-scripts request))}))
