(ns mytomatoes.web
  (:require [compojure.core :refer [routes GET POST wrap-routes]]
            [mytomatoes.actions :as actions]
            [mytomatoes.layout :as layout]
            [mytomatoes.login :refer [redirect-if-not-logged-in wrap-remember-code]]
            [mytomatoes.pages.home :as home]
            [mytomatoes.pages.login :as login]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [ring.middleware.content-type]
            [ring.middleware.cookies]
            [ring.middleware.not-modified]
            [ring.middleware.params]
            [ring.middleware.session]
            [taoensso.timbre :refer [info]]))

(defn app-routes []
  (routes
   (GET "/" request (if (:account-id (:session request))
                      (home/get-page request)
                      (login/get-page request)))
   (POST "/actions/register" request (actions/register request))
   (POST "/actions/login" request (actions/login request))
   (wrap-routes
    (routes
     (POST "/actions/set_preference" request (actions/set-preference request))
     (POST "/actions/logout" [] (actions/logout)))
    redirect-if-not-logged-in)))

(defn include-db-in-request [handler db]
  (fn [req]
    (handler (assoc req :db db))))

(defn create-app [db sessions]
  (-> (app-routes)
      (wrap-remember-code)
      (include-db-in-request db)
      (ring.middleware.params/wrap-params)
      (ring.middleware.session/wrap-session
       {:store (ring.middleware.session.memory/memory-store sessions)})
      (optimus/wrap layout/get-assets
                    optimizations/none
                    strategies/serve-live-assets)
      (ring.middleware.content-type/wrap-content-type)
      (ring.middleware.not-modified/wrap-not-modified)))
