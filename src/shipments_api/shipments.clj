(ns shipments-api.shipments
  (:require [immutant.messaging :as msg]
            [io.pedestal.service.interceptor :as interceptor :refer [defbefore]]
            [validateur.validation :as v]))

(def -pickup-validations
  (v/validation-set
   (v/presence-of :pickup-coordinates)
   (v/presence-of :pickup-contact-name)
   (v/presence-of :pickup-phone)
   (v/presence-of :pickup-time)))

(def -delivery-validations
  (v/validation-set
   (v/presence-of :delivery-coordinates)
   (v/presence-of :delivery-contact-name)
   (v/presence-of :delivery-phone)
   (v/presence-of :delivery-time)))

(defbefore validate-request
  [context]
  (let [request (:request context)
        params (:body-params request)
        is-valid (v/valid? -pickup-validations params)]
    (if (first is-valid)
      (assoc request :shipment params)
      (throw (Exception. (second is-valid))))))

(defn request
  [request]
  (do
    (msg/publish "topic.requested-shipments" (:shipment request))
    {:status 200 :body "Shipment requested."}))
