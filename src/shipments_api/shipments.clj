(ns shipments-api.shipments
  (:require [validateur.validation :as v]
            [immutant.messaging :as msg]
            [io.pedestal.service.interceptor :as interceptor
             :refer [defbefore defhandler]]
            [cheshire.core :refer :all]
            [ring.util.response :as ring-response]))

(def required-fields
  (v/validation-set
   (v/presence-of [:delivery :coordinates]
                  :message "lat,lon coordinates are required.")
   (v/presence-of [:delivery :name]
                  :message "The name of the recipient is required.")
   (v/presence-of [:delivery :phone]
                  :message "The phone of the recipient is required.")
   (v/presence-of [:delivery :time]
                  :message "Please, provide a delivery time.")
   (v/presence-of [:pickup :coordinates]
                  :message "lat,lon coordinates are required.")
   (v/presence-of [:pickup :name]
                  :message "A pickup name is required.")
   (v/presence-of [:pickup :phone]
                  :message "A pickup phone is required.")
   (v/presence-of [:pickup :time]
                  :message "Please provide the time of pickup.")))

(defbefore queue-message
  [context]
  (msg/publish "/topic/shipment-requests" (:json-params (:request context)))
  context)

(defhandler respond-request
  [request]
  {:status 200 :body {:code "Shipment requested."}})
