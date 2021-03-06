(ns immutant.init
  (:require [immutant.web :as web]
            [immutant.messaging :as msg]
            [io.pedestal.service.http :as http]
            [shipments-api.service :as app]))

(msg/start "/topic/shipment-requests")
(web/start-servlet "/" (::http/servlet (http/create-servlet app/service)))
