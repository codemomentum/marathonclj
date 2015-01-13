(ns marathonclj.rest.apps-test
  (:require [clojure.test :refer :all]
            [marathonclj.rest.apps :as apps]
            [marathonclj.rest :as r])
  (import marathonclj.rest.Connection)
  )

(def app-descriptor (read-string (slurp "resources/app-descriptor1.edn")))

(def conn (r/Connection. "http://localhost:8080" {}))

(def version (atom nil))

(deftest crud-functionality
  (testing "verify that no app exists"
    (is (= 0 (count (:apps (apps/get-apps conn))))))
  (testing "create-app"
    (is (= "/001" (:id (apps/create-app conn app-descriptor))))
    )
  (testing "get-app"
    (is (contains? (apps/get-app conn "/001") :app))
    (is (= "ping localhost" (->> (apps/get-app conn "/001") :app :cmd)))
    )
  (testing "list versions"
    (is (string? (do
                   (reset! version (first (:versions (apps/versions conn "001"))))
                   @version)))
    )
  (testing "get-version"
    (is (contains? (apps/version conn "001" @version) :id)))
  (testing "update app"
    (is (= "ping 127.0.0.1" (do
                              (apps/update-app conn "001" {:cmd "ping 127.0.0.1"} true)
                              (->> (apps/get-app conn "/001") :app :cmd))))
    )
  (testing "restart-app"
    (is (= {} (apps/restart-app conn "001" true)))
    )
  (testing "tasks"
    (is (contains? (apps/tasks conn "001") :tasks))
    )
  (testing "kill-tasks"
    (is (contains? (apps/kill-tasks conn "001" {:host "localhost" :scale false}) :tasks))
    )
  (testing "delete-task"
    (is (contains? (apps/kill-task conn "001" false) :tasks))
    )
  (testing "delete app"
    (is (contains? (apps/delete-app conn "001") :deploymentId))
    )
  )

;(apps/create-app conn app-descriptor)
;(apps/tasks conn "001")
;(apps/delete-tasks conn "001" {:host "localhost" :scale false})
;
;(apps/kill-task conn "001" false)



