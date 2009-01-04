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

(def files ["util-test" "core-test" "validation-test" "associations-test"])

(doseq [file files]
  (load file))

(def base-ns (re-find #"^\w*.*\." (str *ns*)))

(apply test-is/run-tests (map #(symbol (str base-ns %)) files))
