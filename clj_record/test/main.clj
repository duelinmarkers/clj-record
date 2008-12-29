(ns clj-record.test.main
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.test-is :as test-is]
            clj-record.config))


(defn drop-tables []
  (try
    (sql/drop-table :manufacturers)
    (sql/drop-table :products)
    (catch Exception e)))

(defn create-tables []
  (sql/create-table :manufacturers
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT manufacturer_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade :real])
  (sql/create-table :products
    [:id "INT" "GENERATED ALWAYS AS IDENTITY CONSTRAINT product_pk PRIMARY KEY"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:price "INT"]
    [:manufacturer_id "INT" "NOT NULL"]))

(sql/with-connection clj-record.config/db
  (sql/transaction
    (drop-tables)
    (create-tables)))

(println "Setup complete.")

(load "util-test")
(load "core-test")
(load "associations-test")
(load "validation-test")
(test-is/run-all-tests)
