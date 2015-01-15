(ns mytomatoes.pages.home
  (:require [clj-time.format :refer [formatter unparse]]
            [hiccup.core :refer [html]]
            [inflections.core :refer [plural]]
            [mytomatoes.layout :refer [with-layout]]
            [mytomatoes.storage :refer [get-tomatoes get-preferences]]))

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
     [:div {:id "congratulations"} "congrats! " [:span "first"] " finished tomato today"]
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

(defn- render-preferences [{:keys [play-ticking use-american-clock]}]
  (html
   [:div {:id "preferences_container"}
    [:div {:id "preferences"}
     [:h3 "preferences"]
     [:ul
      [:li {:id "ticking_preference"}
       [:label
        [:input {:type "checkbox" :name "play_ticking" :checked play-ticking}]
        " Play ticking sound when working on a tomato"]
       [:div {:class "note"} "Not a fan of the ticking? I recommend"
        [:a {:href "http://simplynoise.com" :target "_blank"} "simplynoise.com"] "!"]]
      [:li {:id "clock_preference"}
       [:label
        [:input {:type "checkbox" :name "use_american_clock" :checked use-american-clock}]
        " Use 12-hour clock"]]]]]))

(defn- render-tutorial []
  (html
   [:div {:id "tutorial"}
    [:h3 "how does this work?"]
    [:ul
     [:li {:id "waiting_tutorial"} [:div [:span] [:p "decide upon a task, and start the tomato"]]] " "
     [:li {:id "working_tutorial"} [:div [:span] [:p "work for 25 uninterrupted minutes"]]] " "
     [:li {:id "stop_working_tutorial"} [:div [:span] [:p "stop working when the timer rings"]]] " "
     [:li {:id "enter_description_tutorial"} [:div [:span] [:p "write what you did in a few words"]]] " "
     [:li {:id "on_a_break_tutorial"} [:div [:span] [:p "take a 5 minute break"]]] " "
     [:li {:id "break_over_tutorial"} [:div [:span] [:p "start a new tomato when the timer rings"]]]]]))

(defn pluralize [count s]
  (str count (if (> count 1) (plural s) s)))

(def eurotime (formatter "HH:mm"))
(def ameritime (formatter "KK:mm a"))

(defn- render-tomato [num i tomato]
  (html
   [:li
    [:span {:class "eurotime"}
     (unparse eurotime (:local-start tomato)) " - "
     (unparse eurotime (:local-end tomato))]
    [:span {:class "ameritime"}
     (unparse ameritime (:local-start tomato)) " - "
     (unparse ameritime (:local-end tomato))]
    " "
    (or (not-empty (:description tomato))
        (str "tomato #" (- num i) " finished"))]))

(defn- render-day [[date tomatoes]]
  (let [num (count tomatoes)]
    (html
     [:h3 [:strong (str date)] " " [:span (pluralize num " finished tomato")]]
     [:ul
      (map-indexed (partial render-tomato num) tomatoes)])))

(defn- render-completed-tomatoes [tomatoes prefs]
  (let [days (group-by :date tomatoes)]
    (html
     [:div {:id "done"}
      [:div {:class (if (:use-american-clock prefs)
                      "american_clock"
                      "european_clock")}
       (map render-day days)]])))

(defn- render-audio []
  (html
   [:div {:id "audio"}
    [:audio {:id "alarm_audio" :autobuffer true :preload "auto"}
     [:source {:src "sounds/alarm.ogg" :type "audio/ogg;codecs=vorbis"}]
     [:source {:src "sounds/alarm.mp3" :type "audio/mpeg;codecs=mp3"}]
     [:source {:src "sounds/alarm.wav" :type "audio/x-wav;codecs=1"}]]
    [:audio {:id "ticking_audio_1" :autobuffer true :preload "auto"}
     [:source {:src "sounds/ticking.ogg" :type "audio/ogg;codecs=vorbis"}]
     [:source {:src "sounds/ticking.mp3" :type "audio/mpeg;codecs=mp3"}]
     [:source {:src "sounds/ticking.wav" :type "audio/x-wav;codecs=1"}]]
    [:audio {:id "ticking_audio_2" :autobuffer true :preload "auto"}
     [:source {:src "sounds/ticking.ogg" :type "audio/ogg;codecs=vorbis"}]
     [:source {:src "sounds/ticking.mp3" :type "audio/mpeg;codecs=mp3"}]
     [:source {:src "sounds/ticking.wav" :type "audio/x-wav;codecs=1"}]]]))

(defn get-page [{:keys [db session] :as request}]
  (let [tomatoes (get-tomatoes db (:account-id session))
        prefs (get-preferences db (:account-id session))]
    (with-layout request
      {:body
       (str (render-states)
            (render-preferences prefs)
            (when-not (:hide-tutorial prefs) (render-tutorial))
            (render-completed-tomatoes tomatoes prefs)
            (render-audio))
       :script-bundles ["home.js"]})))
