(ns anticafe.auth
  (:require
    [anticafe.utils :refer [>evt]]
    [oops.core :refer [oget ocall!]]
    [re-frame.core :as rf]))

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

(defn- set-network [effects ethereum]
  (assoc-in effects [:db :auth :network] (get networks (:chain-id ethereum))))

(rf/reg-sub
  :auth/network
  (fn [db _]
    (get-in db [:auth :network])))

(rf/reg-sub
  :auth/valid-network?
  :<- [:auth/network]
  (fn [network]
    (= network :rinkeby)))

(rf/reg-cofx
  :auth/ethereum
  (fn [cofx _]
    (let [metamask? (oget js/window "?ethereum.?isMetaMask")
          chain-id  (oget js/window "?ethereum.?chainId")]
      (assoc cofx :ethereum {:metamask? metamask?
                             :chain-id chain-id}))))

(rf/reg-fx
  :auth/listen-ethereum
  (fn [{:keys [trigger handler]}]
    (-> js/window
        (oget "ethereum")
        (ocall! "on" trigger handler))))

(rf/reg-event-fx
  :auth/change-chain
  [(rf/inject-cofx :auth/ethereum)]
  (fn [{:keys [db ethereum]} _]
    (-> {:db db}
        (update-in [:db :auth :listeners] #(-> % (conj "chainChanged") set))
        (set-network ethereum))))

(defn- listening? [db trigger]
  (-> db
      (get-in [:auth :listeners])
      (contains? trigger)))

(rf/reg-event-fx
  :auth/init
  [(rf/inject-cofx :auth/ethereum)]
  (fn [{:keys [db ethereum]} _]
    (cond-> {:db db :fx []}
      :always
      (-> (assoc-in [:db :auth :metamask-installed?]
                    (boolean (:metamask? ethereum)))
          (set-network ethereum))

      (and (:metamask? ethereum)
           (not (listening? db "chainChanged")))
      (update :fx conj [:auth/listen-ethereum
                        {:trigger "chainChanged"
                         :handler #(>evt [:auth/change-chain])}]))))
