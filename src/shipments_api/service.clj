(ns shipments-api.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http :refer [json-body]]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [shipments-api.shipments :as shipments]
              [shipments-api.validations :as v]))

(defn home-page
  [request]
  {:status 200 :body "Hi"})

(defroutes routes
  [[["/" {:get home-page}

     ;; Set default interceptors for /about and any other paths under /
     ^:interceptors [(body-params/body-params
                      (body-params/default-parser-map
                        :json-options {:key-fn keyword}))
                     json-body]

     ["/shipments" {:post [^:interceptors
                           [(v/validate-request
                             shipments/required-fields :json-params)
                            shipments/queue-message]
                           :shipment-request shipments/respond-request]}]]]])

;; Consumed by shipments-api.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty or :tomcat (see comments in project.clj
              ;; to enable Tomcat)
              ;;::bootstrap/host "localhost"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
