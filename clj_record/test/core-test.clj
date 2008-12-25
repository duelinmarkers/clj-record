(ns clj_record.test.core-test
  (:require
    [clj_record.test.model.manufacturer :as manufacturer]
    [clj_record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest table-name-answers-based-on-model-namespace
  (are (= _1 _2)
    "manufacturer"  (manufacturer/table-name)
    "product"       (product/table-name)))

(deftest find-record-based-on-id
  (let [humedai (manufacturer/create {:name "Humedai Motors"})]
    (is (= humedai (manufacturer/find-record (:id humedai))))
    (manufacturer/destroy humedai)))

(deftest find-records-based-on-attribute-equality-conditions
  (let [humedai (manufacturer/create {:name "Humedai Motors"})
        other-1 (manufacturer/create {:name "Some Other"})
        results (manufacturer/find-records {:name "Humedai Motors"})]
    (is (= [humedai] results))
    (manufacturer/destroy humedai)
    (manufacturer/destroy other-1)))
