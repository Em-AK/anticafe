(ns anticafe.utils
  (:require
    [clojure.string :as cstr]
    [re-frame.core :refer [subscribe dispatch]]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

(defn shorten [address]
  (when (seq address)
    (str (cstr/join (take 6 address))
         "..."
         (cstr/join (take-last 4 address)))))
