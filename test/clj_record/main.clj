(ns clj-record.main
  (:require [clojure.contrib.sql :as sql]
            [clojure.test :as test-is])
  (:use clj-record.test-model.config
        clojure.contrib.str-utils))


(defmulti get-id-key-spec :subprotocol)
(defmethod get-id-key-spec "derby" [db-spec name]
  [:id :int (str "GENERATED ALWAYS AS IDENTITY CONSTRAINT " name " PRIMARY KEY")])
(defmethod get-id-key-spec :default [db-spec name]
  [:id "SERIAL UNIQUE PRIMARY KEY"])

(defn drop-tables []
  (try
    (sql/drop-table :manufacturers)
    (sql/drop-table :productos)
    (catch Exception e)))

(defn create-tables []
  (sql/create-table :manufacturers
    (get-id-key-spec db "manufacturer_pk")
    [:name    "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade   :int])
  (sql/create-table :productos
    (get-id-key-spec db "product_pk")
    [:name            "VARCHAR(32)" "NOT NULL"]
    [:price           :int]
    [:manufacturer_id :int "NOT NULL"])
  (sql/create-table :thing_one
    (get-id-key-spec db "thing_one_pk")
    [:name            "VARCHAR(32)" "NOT NULL"])
  (sql/create-table :thing_two
    (get-id-key-spec db "thing_two_pk")
    [:thing_one_id   :int "NOT NULL"]))

(defn reset-db []
  (println "resetting" (db :subprotocol))
  (sql/with-connection db
    (sql/transaction
      (drop-tables)
      (create-tables)))
  (println "database reset"))

(defn load-and-run
  "Resets the test database, then finds, loads, and runs all the tests."
  []
  (reset-db)
  (let [test-files  (for [f (.listFiles (java.io.File. "clj_record"))
                          :when (re-find #"test.clj$" (.getPath f))]
                      (re-find #"[^.]+" (.getPath f)))
        test-namespaces (map #(symbol (re-gsub #"/" "." (re-gsub #"_" "-" %))) test-files)]
    (doseq [file test-files]
      (load file))
    (apply test-is/run-tests test-namespaces)))

