(ns clj-record.test.core-test
  (:require
    [clj-record.core :as core]
    [clj-record.test.model.manufacturer :as manufacturer]
    [clj-record.test.model.product :as product])
  (:use clojure.contrib.test-is))


(deftest table-name-is-permissive-about-input-type
  (are (= _1 (core/table-name _2))
    "foos" "foo"
    "foos" 'foo
    "foos" :foo))

(deftest table-name-answers-based-on-model-namespace
  (are (= _1 _2)
    "manufacturers"  (manufacturer/table-name)
    "products"       (product/table-name)))

(deftest get-record-by-id
  (let [humedai (manufacturer/create {:name "Humedai Motors"})]
    (is (= humedai (manufacturer/get-record (:id humedai))))
    (manufacturer/destroy-record humedai)))

(deftest find-records-by-attribute-equality-conditions
  (let [humedai (manufacturer/create {:name "Humedai Motors"})
        other-1 (manufacturer/create {:name "Some Other"})
        results (manufacturer/find-records {:name "Humedai Motors"})]
    (is (= [humedai] results))
    (manufacturer/destroy-record humedai)
    (manufacturer/destroy-record other-1)))

(deftest destroy-record-destroys-by-id-from-record
  (let [humedai (manufacturer/create {:name "Humedai Motors"})]
    (manufacturer/destroy-record {:id (:id humedai)})
    (is (empty? (manufacturer/find-records {:id (:id humedai)})))))

(deftest update-uses-id-to-update-other-given-attributes-leaving-unspecified-attributes-untouched
  (let [humedai (manufacturer/create {:name "Humedai Motors" :grade 90})
        id (:id humedai)]
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
