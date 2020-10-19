(ns anticafe.web.main
  (:require
    [anticafe.core]
    [anticafe.utils :refer [<sub >evt]]
    [clojure.string :as cstr]
    [goog.dom :as gdom]
    [reagent.dom :as rdom]))

(defn- header []
  [:header
   [:h2 "Welcome to the decentralized Anti-café!"]
   [:em "Relax, have fun and pay for the time spent "
    [:strong "in real time."]]])

(defn- my-network [network]
  (when network
    [:p "You are on network "
     [:strong (cstr/capitalize (name network))]]))

(defn- connect-button [{:keys [status address on-connect]}]
  [:div
   (case status
     :connected        [:p "Connected with account " [:strong [:code address]]]
     :please-connect   [:button {:on-click on-connect} "Connect"]
     :wrong-network    [:p "Please switch your wallet to the "
                        [:strong "Rinkeby Test Network."]]
     :install-metamask [:p "To get started "
                        [:strong
                         [:a {:href "https://metamask.io/download.html"
                              :target :_blank
                              :rel :noopener}
                          "Install MetaMask"]]])])

(defn- main-content []
  [:main
   [my-network (<sub [:auth/network])]
   [connect-button {:status (<sub [:auth/status])
                    :address (<sub [:auth/short-address])
                    :on-connect #(>evt [:auth/connect])}]])

(defn- footer []
  [:footer
   [:a {:href "https://github.com/Em-AK/anticafe"
        :rel :nofollow}
    "Source Code"]
   [:a {:href "https://app.netlify.com/sites/goofy-mahavira-0ddc1f/deploys"
        :rel :nofollow}
    [:img {:src "https://camo.githubusercontent.com/503b99e52eb8297e2ac6346992ca05f2d6d09e24/68747470733a2f2f6170692e6e65746c6966792e636f6d2f6170692f76312f6261646765732f65356530383132342d646363332d346262642d613462632d3330656237633236326664382f6465706c6f792d737461747573"
           :alt "Netlify Status"
           :data-canonical-src "https://api.netlify.com/api/v1/badges/e5e08124-dcc3-4bbd-a4bc-30eb7c262fd8/deploy-status"
           :style {:max-width "100%"}}]]])

(defn dapp []
  [:<>
   [:div.content
    [header]
    [main-content]]
   [footer]])

(defn ^:dev/after-load render! []
  (rdom/render [dapp]
               (gdom/getElement "dapp")))

(defn main! []
  (>evt [:auth/init])
  (render!))
