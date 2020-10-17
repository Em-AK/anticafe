(ns anticafe.web.main
  (:require
    [reagent.dom :as rdom]))

(defn home []
  [:<>
   [:h2 "Welcome to the decentralized Anti-café!"]
   [:p "Relax, have fun and pay for the time spent in real time."]
   [:a {:href "#"} "Get started"]])

(defn main! []
  (rdom/render [home] (js/document.getElementById "dapp")))

(defn ^:dev/after-load reload! []
  (main!))
