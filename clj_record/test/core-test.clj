(ns clj_record.test.core-test
  (:require
    [clj_record.core :as core]
    [clj_record.test.model.manufacturer :as manufacturer]
    [clj_record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest table-name-answers-based-on-model-namespace
  (are (= _1 _2)
    "manufacturer"  (manufacturer/table-name)
    "product"       (product/table-name)))

(deftest find-record-by-id
  (let [humedai (manufacturer/create {:name "Humedai Motors"})]
    (is (= humedai (manufacturer/find-record (:id humedai))))
    (manufacturer/destroy-record humedai)))

(deftest find-records-by-attribute-equality-conditions
  (let [humedai (manufacturer/create {:name "Humedai Motors"})
        other-1 (manufacturer/create {:name "Some Other"})
        results (manufacturer/find-records {:name "Humedai Motors"})]
    (is (= [humedai] results))
    (manufacturer/destroy-record humedai)
    (manufacturer/destroy-record other-1)))

(deftest destroy-record-destroys-by-id-from-record
  (let [humedai (manufacturer/create {:name "Humedai Motors"})]
    (manufacturer/destroy-record {:id (:id humedai)})
    (is (empty? (manufacturer/find-records {:id (:id humedai)})))))

(deftest to-conditions
  (are (= _1 (core/to-conditions _2))
    "a = 1" {:a 1}
    "a = 'one'" {:a "one"}
    "a = 1 AND b = 2" [[:a 1] [:b 2]] ; Vector makes order reliable.
    "a IS NULL" {:a nil}))
