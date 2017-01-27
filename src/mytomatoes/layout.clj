(ns mytomatoes.layout
  (:require [clojure.java.io :as io]
            [hiccup.core :refer [html]]
            [hiccup.page :as page]
            [mytomatoes.storage :refer [get-preferences]]
            [optimus.assets :as assets]
            [optimus.html]))

(def bundles {"styles.css" ["/theme/css/reset.css"
                            "/theme/css/master.css"]
              "lib.js" ["/javascript/external/jquery.js"
                        "/javascript/external/jquery.color.js"
                        "/javascript/external/jquery.url.js"
                        "/javascript/external/shortcut.js"
                        "/javascript/external/date.js"
                        "/javascript/external/AC_RunActiveContent.js"
                        "/javascript/library.js"
                        "/javascript/ajax_service.js"]
              "login.js" ["/javascript/input_hints.js"
                          "/javascript/register.js"]
              "home.js" ["/javascript/countdown.js"
                         "/javascript/animation.js"
                         "/javascript/sound_player.js"
                         "/javascript/preferences.js"
                         "/javascript/index.js"
                         "/javascript/bootstrap.js"]
              "recovery.js" ["/javascript/recovery.js"]
              "change-password.js" ["/javascript/input_hints.js"
                                    "/javascript/change-password.js"]})

(defn get-assets []
  (concat
   (assets/load-bundles "public" bundles)
   (assets/load-assets "public" ["/favicon.ico"
                                 "/mp3player.swf"
                                 "/theme/images/error.gif"
                                 #"/sounds/.+\.(mp3|ogg|wav)"])))

(def banner
  #_{:id "donations-2015"
   :contents (html [:p "One more year of mytomatoes has been secured by "
                    [:a {:target "_blank" :href "https://www.gofundme.com/jadh879w"}
                     "45 wonderful donors"]
                    " - thank you so much!"])}
  {:id "browser-notifications"
   :contents (html [:p "mytomatoes now supports browser notifications for alerts without sound - you can enable them under preferences"])})

(defn hide-banner [request]
  (when-let [account-id (:account-id (:session request))]
    ((keyword (str "hide-banner-" (:id banner)))
     (get-preferences (:db request) account-id))))

(defn with-layout [request page]
  {:headers {"Content-Type" "text/html; charset=utf-8"}
   :status (:status page 200)
   :body (page/html5
          [:head
           [:meta {:charset "utf-8"}]
           [:title "mytomatoes.com"]
           (optimus.html/link-to-css-bundles request ["styles.css"])]
          [:body
           [:div {:id "main"}
            [:div {:id "header"}
             (when (:account-id (:session request))
               [:form {:id "logout" :method "POST", :action "/actions/logout"}
                [:input {:type "submit" :value "log out"}]])
             [:h1 "mytomatoes.com"
              [:div " simple pomodoro tracking"]]]

            (when (and banner (not (hide-banner request)))
              [:div {:id "banner"}
               (when (:account-id (:session request))
                 [:a {:id "hide_banner" :href "#" :data-id (:id banner)}
                  "hide"
                  [:span " this banner"]])
               (:contents banner)])

            [:noscript
             [:style {:type "text/css"} "#states, #done, #welcome {display: none;}"]
             [:div {:id "noscript"}
              [:p
               "mytomatoes.com is a tool for use with the "
               [:a {:href "http://www.pomodorotechnique.com/"} "pomodoro technique"]
               " by "
               [:a {:href "http://francescocirillo.com/"} "Francesco Cirillo"]
               ". "
               [:em "It doesn't work without Javascript."]
               " Sorry."]]]

            (:body page)

            [:div {:id "push"}]]
           [:div {:id "footer"}
            [:a {:target "_blank" :href "http://www.pomodorotechnique.com/"}
             "read about the pomodoro technique"]
            (if (= 500 (:status page))
              " - and twitter your rage to "
              " - and twitter your feedback to ")
            [:a {:target "_blank" :href "http://twitter.com/mytomatoes"}
             "@mytomatoes"]
            (if (= 500 (:status page))
              " &gt;&lt;"
              " :-)")]]
          (optimus.html/link-to-js-bundles request (into ["lib.js"] (:script-bundles page)))
          (when (= :prod (:env request))
            [:script (slurp (io/resource "public/ga.js"))]))})
