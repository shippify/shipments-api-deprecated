(ns shipments-api.test-utils
  (:require [ring.util.codec :only [url-encode] :as rutils]))

(defn make-query-string [m]
  (->> (for [[k v] m]
         (str (rutils/url-encode k) "=" (rutils/url-encode v)))
       (interpose "&")
       (apply str)))
