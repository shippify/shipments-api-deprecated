(ns shipments-api.shipments-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [shipments-api.service :as service]
            [shipments-api.test-utils :as utils]
            [immutant.messaging :as msg]
            [cheshire.core :refer :all]))

(def shipment {:delivery {:coordinates "1,1"
                          :name "Eduardo Raad"
                          :phone "59399229922"
                          :time "2012-04-15T18:06:08-07:00"}
               :pickup {:coordinates "1,1"
                        :name "Eduardo Raad"
                        :phone "59399229922"
                        :time "2012-04-15T18:06:08-07:00"}})

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest shipment-post-test
  (testing "Posting a well-formed JSON to /shipments injects a message into a topic"
    (let [expected (promise)
          observer (msg/listen "/topic/shipment-requests" #(deliver expected %))
          response (response-for service
                                 :post "/shipments"
                                 :body (generate-string shipment)
                                 :headers {"Content-Type" "application/json"})]
      (is (= (:status response) 200))
      (is (= (:headers response) {"Content-Type" "application/json;charset=UTF-8"}))
      (is (= (:body response) "{\"code\":\"Shipment requested.\"}"))
      (is (= shipment (deref expected 1000 :fail)))
      (msg/unlisten observer))))

(deftest error-shipment-post-test
  (testing "Posted JSON body needs to be well-formed"
    (let [response (response-for service
                                 :post "/shipments"
                                 :body (generate-string {:a 1}))]
      (is (= (:status response) 400))
      (is (= (contains? (parse-string (:body response) true) :error) true)))))
