(ns clj-record.test.validation-test
  (:require
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest validate-returns-empty-for-a-model-without-validations
  (is (empty? (product/validate {}))))

(deftest validate-returns-empty-for-a-valid-record
  (is (empty? (manufacturer/validate {:name "Good Name"}))))

(deftest validate-returns-single-message-keyed-by-attribute-for-an-invalid-record
  (is (= {:name ["empty!"]} (manufacturer/validate {:name ""}))))

(deftest validate-with-multiple-messages-for-one-attribute
  (is (= 
    {:name ["starts with whitespace!" "ends with whitespace!"]}
    (manufacturer/validate {:name " Bad Name "}))))

(deftest validation-function-can-be-defined-in-model-namespace
  (is (= ["negative!"] (:grade (manufacturer/validate {:grade -2 :name "Bob"})))))

(deftest validate-with-errors-on-multiple-attributes
  (is (=
    {:name ["empty!"] :grade ["negative!"]}
    (manufacturer/validate {:name "" :grade -2}))))
