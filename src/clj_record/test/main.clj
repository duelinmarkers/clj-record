(ns clj-record.test.main
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.test-is :as test-is]
            [clojure.contrib.str-utils :as str-utils]
            clj-record.test.model.config))


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
    (get-id-key-spec clj-record.test.model.config/db "manufacturer_pk")
    [:name    "VARCHAR(32)" "NOT NULL"]
    [:founded "VARCHAR(4)"]
    [:grade   :int])
  (sql/create-table :productos
    (get-id-key-spec clj-record.test.model.config/db "product_pk")
    [:name            "VARCHAR(32)" "NOT NULL"]
    [:price           :int]
    [:manufacturer_id :int "NOT NULL"]))

(def this-file *file*)

(defn load-and-run
  "Resets the test database, then finds, loads, and runs all the tests."
  []
  (sql/with-connection clj-record.test.model.config/db
    (sql/transaction
      (drop-tables)
      (create-tables)))
  (println (str (clj-record.test.model.config/db :subprotocol) " database setup complete."))
  (let [test-files  (for [f (.listFiles (java.io.File. "clj_record/test"))
                          :when (re-find #"test.clj$" (.getPath f))]
                      (re-find #"[^.]+" (.getPath f)))
        test-namespaces (map #(symbol (str-utils/re-gsub #"/" "." (str-utils/re-gsub #"_" "-" %))) test-files)]
    (doseq [file test-files]
      (load file))
    (apply test-is/run-tests test-namespaces)))

