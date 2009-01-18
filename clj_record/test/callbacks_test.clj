(ns clj-record.test.callbacks-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer])
  (:use clojure.contrib.test-is))


(deftest before-save-can-transform-the-record-before-create
  (let [m (manufacturer/create {:name "A" :founded "68"})]
    (is (= "1968" (m :founded)))
    (manufacturer/destroy-record m)))
