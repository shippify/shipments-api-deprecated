(ns shipments-api.shipments-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [shipments-api.service :as service]
            [shipments-api.test-utils :as utils]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest shipment-post-test
  (is (=
       (:status (response-for service :post "/shipments"
                              :body (utils/make-query-string {:a 1})))
       200))
  (is (=
       (:headers (response-for service :post "/shipments"
                               :body (utils/make-query-string {:a 1})))
       {"Content-Type" "application/json;charset=UTF-8"})))
