(ns clj-record.test.core-test
  (:require
    [clj-record.core :as core]
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is
        clj-record.test.test-helper))


(deftest table-name-can-be-unconventional-with-table-name-option-to-init-model
  (is (= "productos" (core/table-name "product"))))

(deftest table-name-is-available-on-each-model-namespace
  (are (= _1 _2)
    "manufacturers"  (manufacturer/table-name)
    "productos"       (product/table-name)))

(defdbtest insert-returns-id-of-new-record
  (let [id (manufacturer/insert (valid-manufacturer-with {:name "ACME"}))
        acme (manufacturer/get-record id)]
    (is (= "ACME" (acme :name)))))

(defdbtest get-record-by-id
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))]
    (is (= humedai (manufacturer/get-record (humedai :id))))))

(defdbtest get-record-throws-if-not-found
  (is (thrown? IllegalArgumentException (manufacturer/get-record -1))))

(defdbtest find-records-by-attribute-equality-conditions
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))
        other-1 (manufacturer/create (valid-manufacturer-with {:name "Some Other"}))]
    (is (= [humedai] (manufacturer/find-records {:name "Humedai Motors"})))))

(defdbtest find-records-by-SQL-conditions
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))
        other-1 (manufacturer/create (valid-manufacturer-with {:name "Some Other"}))]
    (is (= [humedai] (manufacturer/find-records ["name = ?" "Humedai Motors"])))))

(defdbtest destroy-record-destroys-by-id-from-record
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors"}))]
    (manufacturer/destroy-record {:id (humedai :id)})
    (is (empty? (manufacturer/find-records {:id (humedai :id)})))))

(defdbtest update-uses-id-to-update-other-given-attributes-leaving-unspecified-attributes-untouched
  (let [humedai (manufacturer/create (valid-manufacturer-with {:name "Humedai Motors" :grade 90}))
        id (humedai :id)]
    (manufacturer/update {:id id :name "Schmoomdai Motors" :founded "2008"})
    (is (= 
      {:name "Schmoomdai Motors" :grade 90 :founded "2008"}
      (select-keys (manufacturer/get-record id) [:name :grade :founded])))))

(deftest to-conditions
  (are (= _1 (core/to-conditions _2))
    ["a = ?" 1] {:a 1}
    ["a = ?" "one"] {:a "one"}
    ["a IS NULL"] {:a nil})
  (let [r (core/to-conditions {:a 1 :b 2})]
    (is (or (= r ["a = ? AND b = ?" 1 2]) (= r ["b = ? AND a = ?" 2 1])))))

(deftest model-metadata-with-no-args
  (is (= 
    (@clj-record.meta/all-models-metadata "manufacturer")
    (manufacturer/model-metadata)))
  (is (contains? @(manufacturer/model-metadata) :validations)))

(deftest model-metadata-with-one-arg
  (is (map? (manufacturer/model-metadata :callbacks))))
