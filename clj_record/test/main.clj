(ns clj-record.test.main
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.test-is :as test-is]
            [clojure.contrib.str-utils :as str-utils]
            clj-record.test.model.config))

;; Multiple DB Support
;;--------------------------------------------------
(defmulti get-id-key-spec (fn [{sb :subprotocol} _ ] (.toUpperCase sb)))

(defmethod get-id-key-spec "DERBY" [db-spec name]
  [:id :int (str "GENERATED ALWAYS AS IDENTITY CONSTRAINT " name " PRIMARY KEY")])

(defmethod get-id-key-spec "POSTGRESQL" [db-spec name]
  [:id (str "SERIAL UNIQUE PRIMARY KEY")])

(defmethod get-id-key-spec "MYSQL" [db-spec name]
  [:id :serial])
;;--------------------------------------------------

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

(sql/with-connection clj-record.test.model.config/db
  (sql/transaction
    (drop-tables)
    (create-tables)))

(println "DB setup complete.")

(let [test-files  (for [f (-> *file* java.io.File. .getParentFile .list)
                        :when (re-find #"test.clj$" f)]
                    (re-find #"[^.]+" f))
      base-namespace (re-find #"^\w*.*\." (str *ns*))
      test-namespaces (map #(symbol (str base-namespace (str-utils/re-gsub #"_" "-" %))) test-files)]
  (doseq [file test-files]
    (load file))
  (apply test-is/run-tests test-namespaces))
