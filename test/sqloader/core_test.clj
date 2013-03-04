(ns sqloader.core-test
  (:use clojure.test
        sqloader.core))

(deftest test-basic-column-definition
  (testing "basic column definition"
    (is (= "name varchar(10)" 
           (column-definition ["name" {:type "varchar(10)"}])))))

