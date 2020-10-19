(ns anticafe.auth
  (:require
    [anticafe.utils :as utils :refer [>evt]]
    [oops.core :refer [oget ocall! ocall]]
    [re-frame.core :as rf]))

;; Effect handlers

(rf/reg-fx
  :auth/listen-ethereum
  (fn [{:keys [trigger handler]}]
    (-> js/window
        (oget "ethereum")
        (ocall! "on" trigger (comp handler js->clj)))))

(rf/reg-fx
  :auth/request-ethereum
  (fn [{:keys [method on-success on-error]}]
    (-> js/window
        (oget "ethereum")
        (ocall "request" (clj->js {:method method}))
        (.then (comp on-success js->clj))
        (.catch on-error))))

;; Coeffect handlers

(rf/reg-cofx
  :auth/ethereum
  (fn [cofx _]
    (let [metamask? (oget js/window "?ethereum.?isMetaMask")
          chain-id  (oget js/window "?ethereum.?chainId")]
      (assoc cofx :ethereum {:metamask? metamask?
                             :chain-id chain-id}))))

;; Ethereum network

(def networks
  {"0x1"  :mainnet
   "0x3"  :ropsten
   "0x4"  :rinkeby
   "0x5"  :goerli
   "0x2a" :kovan})

(rf/reg-sub
  :auth/metamask-installed?
  (fn [db _]
    (get-in db [:auth :metamask-installed?])))

(rf/reg-sub
  :auth/network
  (fn [db _]
    (get-in db [:auth :network])))

(defn- set-network [effects chain-id]
  (assoc-in effects [:db :auth :network] (get networks chain-id)))

(rf/reg-sub
  :auth/valid-network?
  :<- [:auth/network]
  (fn [network]
    (= network :rinkeby)))

(rf/reg-event-db
  :auth/change-chain
  (fn [db [_ chain-id]]
    (-> {:db db} (set-network chain-id) :db)))

(rf/reg-event-fx
  :auth/init
  [(rf/inject-cofx :auth/ethereum)]
  (fn [{:keys [db ethereum]} _]
    (cond-> {:db db :fx []}
      :always
      (-> (assoc-in [:db :auth :metamask-installed?]
                    (boolean (:metamask? ethereum)))
          (set-network (:chain-id ethereum)))

      (:metamask? ethereum)
      (update :fx
              conj
              [:auth/listen-ethereum
               {:trigger "chainChanged"
                :handler #(>evt [:auth/change-chain %])}]
              [:auth/listen-ethereum
               {:trigger "accountsChanged"
                :handler #(>evt [:auth/change-account %])}]))))

;; User account

(rf/reg-sub
  :auth/account
  (fn [db _]
    (get-in db [:auth :accounts 0])))

(rf/reg-sub
  :auth/short-address
  :<- [:auth/account]
  (fn [address]
    (utils/shorten address)))

(rf/reg-event-db
  :auth/change-account
  (fn [db [_ accounts]]
    (assoc-in db [:auth :accounts] accounts)))

(rf/reg-event-fx
  :auth/connect
  (fn [{db :db}]
    {:db db
     :fx [[:auth/request-ethereum
           {:method "eth_requestAccounts"
            :on-success #(>evt [:auth/change-account %])
            :on-error #(js/console.error "eth_requestAccounts: " %)}]]}))

;; Auth status

(rf/reg-sub
  :auth/status
  :<- [:auth/account]
  :<- [:auth/valid-network?]
  :<- [:auth/metamask-installed?]
  (fn [[account
        valid-network?
        metamask-installed?] _]
    (cond
      (and account valid-network?) :connected
      valid-network?               :please-connect
      metamask-installed?          :wrong-network
      :else                        :install-metamask)))
