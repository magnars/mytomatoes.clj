(ns mytomatoes.web
  (:require [compojure.core :refer [routes GET POST]]
            [mytomatoes.actions :as actions]
            [mytomatoes.layout :as layout]
            [mytomatoes.pages.login :as login]
            [mytomatoes.pages.home :as home]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [ring.middleware.content-type]
            [ring.middleware.cookies]
            [ring.middleware.not-modified]
            [ring.middleware.params]
            [ring.middleware.session]))

(defn app-routes []
  (routes
   (GET "/" request (if (:account-id (:session request))
                      (home/get-page request)
                      (login/get-page request)))
   (POST "/actions/register" request (actions/register request))
   (POST "/actions/login" request (actions/login request))))

(defn include-db-in-request [handler db]
  (fn [req]
    (handler (assoc req :db db))))

(defn create-app [db sessions]
  (-> (app-routes)
      (include-db-in-request db)
      (optimus/wrap layout/get-assets
                    optimizations/none
                    strategies/serve-live-assets)
      (ring.middleware.content-type/wrap-content-type)
      (ring.middleware.not-modified/wrap-not-modified)
      (ring.middleware.params/wrap-params)
      (ring.middleware.cookies/wrap-cookies)
      (ring.middleware.session/wrap-session
       {:store (ring.middleware.session.memory/memory-store sessions)})))
