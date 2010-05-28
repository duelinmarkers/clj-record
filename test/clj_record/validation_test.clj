(ns clj-record.validation-test
  (:require
    [clj-record.validation :as validation]
    [clj-record.test-model.manufacturer :as manufacturer]
    [clj-record.test-model.product :as product])
  (:use clojure.test))


(deftest validate-returns-valid-for-a-model-without-validations
  (is (validation/valid? (product/validate {}))))

(deftest validate-returns-valid-for-a-valid-record
  (is (validation/valid? (manufacturer/validate {:name "Good Name"}))))

(deftest validate-returns-invalid-for-an-invalid-record
  (is (not (validation/valid? (manufacturer/validate {:name ""})))))

(deftest exposes-single-message-by-attribute-for-an-invalid-record
  (is (= ["empty!"] (validation/messages-for (manufacturer/validate {:name ""}) :name))))

(deftest validate-with-multiple-messages-for-one-attribute
  (is (= 
    ["starts with whitespace!" "ends with whitespace!"]
    (validation/messages-for (manufacturer/validate {:name " Bad Name "}) :name))))

(deftest validation-message-can-be-defined-in-model-namespace
  (is (= ["negative!"] (:grade (manufacturer/validate {:grade -2 :name "Bob"})))))

(deftest validate-with-errors-on-multiple-attributes
  (let [validation-result (manufacturer/validate {:name "" :founded "oh"})]
    (are [messages attribute-name] (= messages (validation/messages-for validation-result attribute-name))
      ["empty!"] :name
      ["must be numeric"] :founded)))
