(ns shipments-api.validations
  (:require [validateur.validation :as v]
            [io.pedestal.service.interceptor :as interceptor
             :refer [definterceptorfn]]
            [ring.util.response :as ring-response]))

(definterceptorfn validate-request
  [validation-set params-key]
  (interceptor/before
   ::validate-parameters
   (fn [{:keys [request response] :as context}]
     (let [params (params-key request)]
       (if (v/valid? validation-set params)
         context
         (assoc-in context [:response]
                   (-> (ring-response/response {:error "Invalid request."})
                       (ring-response/status 400))))))))
