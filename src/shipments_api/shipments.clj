(ns shipments-api.shipments
  (:require [immutant.messaging :as msg]
            [io.pedestal.service.interceptor :as interceptor :refer [defbefore]]))

(defbefore validate-request
  [context]
  (let [request (:request context)]
    ))

(defn request
  [request]
  (msg/publish "topic.requested-shipments" (:shipment request)))
