(ns clj_record.test.assocations-test
  (:require
    [model.manufacturer :as manufacturer]
    [model.product :as product])
  (:use clojure.contrib.test-is))

(deftest belongs-to-creates-find-function
  (let [humedai (manufacturer/create {:name "Humedai Automotive"})
        s3000xi (product/create {:name "S-3000xi" :manufacturer_id (:id humedai)})]
    (is (= humedai (product/find-manufacturer s3000xi)))))
