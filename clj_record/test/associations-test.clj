(ns clj-record.test.assocations-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest belongs-to-creates-find-function
  (let [humedai (manufacturer/create {:name "Humedai Automotive"})
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})]
    (is (= humedai (product/find-manufacturer s3000xi)))
    (product/destroy-record s3000xi)
    (manufacturer/destroy-record humedai)))

(deftest has-many-creates-a-find-function
  (let [humedai (manufacturer/create {:name "Humedai Automotive"})
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})
        s3000xl (product/create {:name "S-3000xl" :manufacturer_id (:id humedai)})]
    (is (= [s3000xi s3000xl] (manufacturer/find-products humedai)))
    (product/destroy-record s3000xi)
    (product/destroy-record s3000xl)
    (manufacturer/destroy-record humedai)))

(deftest has-many-creates-a-destroy-function
  (let [humedai (manufacturer/create {:name "Humedai Automotive"})
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})
        s3000xl (product/create {:name "S-3000xl" :manufacturer_id (:id humedai)})]
    (manufacturer/destroy-products humedai)
    (is (empty? (manufacturer/find-products humedai)))
    (manufacturer/destroy-record humedai)))
