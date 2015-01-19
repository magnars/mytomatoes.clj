(ns mytomatoes.pages.error
  (:require [optimus.link :refer [file-path]]
            [hiccup.core :refer [html]]
            [mytomatoes.layout :refer [with-layout]]))

(def error-html
  (html [:p
         "Bah! Something's not right. We've screwed up. An angry email has
                been sent to the admin (unless that's broken aswell, you know,
                Murphys Law and all). Feel free to yell at us on "
         [:a {:href "http://twitter.com/mytomatoes" :target "_blank"}
          "@mytomatoes"]
         ", if that makes you feel better. Or you can "
         [:a {:href "/" :target "_blank"}
          "try again from the beginning"] "."]))

(defn get-page [request]
  (if (:optimus-assets request)
    (with-layout request
      {:body
       (html [:div {:id "error"}
              error-html
              [:img {:src (file-path request "/theme/images/error.gif")}]])
       :status 500})
    {:headers {"Content-Type" "text/html; charset=utf-8"}
     :status 500
     :body error-html}))
