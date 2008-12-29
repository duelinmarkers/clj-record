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
  (is (= {:name ["Name cannot be empty."]} (manufacturer/validate {:name ""}))))

(deftest validate-with-multiple-messages-for-one-attribute
  (is (= 
    {:name ["Name can't start with whitespace." "Name can't end with whitespace."]}
    (manufacturer/validate {:name " Bad Name "}))))
