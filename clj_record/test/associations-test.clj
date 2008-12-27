(ns clj_record.test.assocations-test
  (:require
    [clj_record.test.model.manufacturer :as manufacturer]
    [clj_record.test.model.product :as product])
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

(deftest has-many-creates-a-find-function
  (let [humedai (manufacturer/create {:name "Humedai Automotive"})
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})
        s3000xl (product/create {:name "S-3000xl" :manufacturer_id (:id humedai)})]
    (is (= [s3000xi s3000xl] (manufacturer/find-products humedai)))
    (product/destroy-record s3000xi)
    (product/destroy-record s3000xl)
    (manufacturer/destroy-record humedai)))
