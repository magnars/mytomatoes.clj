(ns mytomatoes.web
  (:require [compojure.core :refer [routes GET POST wrap-routes]]
            [mytomatoes.actions :as actions]
            [mytomatoes.layout :as layout]
            [mytomatoes.login :refer [redirect-if-not-logged-in wrap-remember-code redirect-if-logged-in]]
            [mytomatoes.pages.home :as home]
            [mytomatoes.pages.login :as login]
            [mytomatoes.pages.error :as error]
            [mytomatoes.pages.recovery :as recovery]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [ring.middleware.content-type]
            [ring.middleware.cookies]
            [ring.middleware.not-modified]
            [ring.middleware.params]
            [ring.middleware.session]
            [taoensso.timbre :refer [info error]]
            [ring.util.response :as res]
            [mytomatoes.csv :as csv]))

(defn app-routes []
  (routes
   (GET "/" request (if (:account-id (:session request))
                      (home/get-page request)
                      (login/get-page request)))
   (GET "/error" request (error/get-page request))
   (wrap-routes
    (routes
     (GET "/recovery" request (recovery/get-page request))
     (GET "/change-password" request (recovery/get-change-password-page request))
     (POST "/actions/check-my-words" request (actions/check-my-words request))
     (POST "/actions/register" request (actions/register request))
     (POST "/actions/login" request (actions/login request))
     (POST "/actions/change-password" request (actions/change-password request)))
    redirect-if-logged-in)
   (wrap-routes
    (routes
     (POST "/actions/set_preference" request (actions/set-preference request))
     (POST "/actions/logout" [] (actions/logout))
     (POST "/actions/keep_session_alive" [] (actions/keep-session-alive))
     (POST "/actions/complete_tomato" request (actions/complete-tomato request))
     (GET "/views/completed_tomatoes" request (home/completed-tomatoes-fragment request))
     (GET "/tomatoes.csv" request (csv/render-tomatoes request)))
    redirect-if-not-logged-in)))

(defn include-stuff-in-request [handler db env]
  (fn [req]
    (handler (assoc req :db db :env env))))

(defn wrap-exceptions [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           (error e)
           (error/get-page req)))))

(defn create-app [db sessions env]
  (-> (app-routes)
      (wrap-remember-code)
      (include-stuff-in-request db env)
      (ring.middleware.params/wrap-params)
      (ring.middleware.session/wrap-session
       {:store (ring.middleware.session.memory/memory-store sessions)})
      (wrap-exceptions)
      (optimus/wrap layout/get-assets
                    (if (= :prod env) optimizations/all optimizations/none)
                    (if (= :prod env) strategies/serve-frozen-assets strategies/serve-live-assets))
      (ring.middleware.content-type/wrap-content-type)
      (ring.middleware.not-modified/wrap-not-modified)
      (wrap-exceptions)))
