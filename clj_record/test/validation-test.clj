(ns clj-record.test.validation-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest validate-returns-nil-for-a-model-without-validations
  (is (nil? (product/validate {}))))

(deftest validate-returns-nil-for-a-valid-record
  (is (nil? (manufacturer/validate {:name "Not Empty"}))))

(deftest validate-returns-errors-by-attribute-for-an-invalid-record
  (is (= [["name" "Name cannot be empty."]] (manufacturer/validate {:name ""}))))
