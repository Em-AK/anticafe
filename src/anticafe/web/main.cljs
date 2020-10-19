(ns anticafe.web.main
  (:require
    [anticafe.core]
    [anticafe.utils :refer [<sub >evt]]
    [clojure.string :as cstr]
    [goog.dom :as gdom]
    [reagent.dom :as rdom]))

(defn- shorten [address]
  (str (cstr/join (take 6 address))
       "..."
       (cstr/join (take-last 4 address))))

(defn- connect-button
  [{:keys [account on-connect valid-network? metamask-installed?]}]
  [:div
   (cond
     (and account valid-network?)
     [:div "Connected with account "
      [:strong [:code (shorten account)]]]

     (and metamask-installed? valid-network?)
     [:button {:on-click on-connect} "Connect"]

     metamask-installed?
     [:p "Please switch your wallet to the "
      [:strong "Rinkeby Test Network."]]

     :else
     [:p "To get started "
      [:strong
       [:a {:href "https://metamask.io/download.html"
            :target :_blank
            :rel :noopener}
        "Install MetaMask"]]])])

(defn- my-network [network]
  (when network
    [:p "You are on network "
     [:strong (cstr/capitalize (name network))]]))

(defn home []
  [:<>
   [:h2 "Welcome to the decentralized Anti-café!"]
   [:em "Relax, have fun and pay for the time spent " [:strong "in real time."]]
   [my-network (<sub [:auth/network])]
   [connect-button {:account (<sub [:auth/account])
                    :valid-network? (<sub [:auth/valid-network?])
                    :metamask-installed? (<sub [:auth/metamask-installed?])
                    :on-connect #(>evt [:auth/connect])}]])

(defn ^:dev/after-load render! []
  (rdom/render [home]
               (gdom/getElement "dapp")))

(defn main! []
  (>evt [:auth/init])
  (render!))
